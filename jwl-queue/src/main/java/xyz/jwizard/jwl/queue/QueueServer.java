/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.TypeReference;
import xyz.jwizard.jwl.common.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.common.util.net.NetworkUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class QueueServer implements Closeable {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final String username;
    protected final String password;
    protected final Set<HostPort> nodes;
    protected final SerializerRegistry serializerRegistry;
    protected final ComponentProvider componentProvider;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final QueuePublisher queuePublisher = new QueuePublisher(this);

    protected QueueServer(AbstractBuilder<?> builder) {
        username = builder.username;
        password = builder.password;
        nodes = builder.nodes;
        serializerRegistry = builder.serializerRegistry;
        componentProvider = builder.componentProvider;
    }

    public void start() {
        try {
            if (!running.compareAndSet(false, true)) {
                return;
            }
            if (nodes.isEmpty()) {
                LOG.warn("Not providing any nodes, skipping configuration");
                return;
            }
            final Set<QueueListener<?>> listeners = new HashSet<>(componentProvider
                .getInstancesOf(new TypeReference<>() {
                }));
            LOG.info("Found {} queue listener(s)", listeners.size());
            LOG.info("Queue server start initializing with {} node(s)", nodes.size());
            onStart();
            registerListeners(listeners);
        } catch (Exception ex) {
            throw new CriticalBootstrapException("Queue server connection(s) failed", ex);
        }
    }

    SerializerRegistry getSerializerRegistry() {
        return serializerRegistry;
    }

    public QueuePublisher getQueuePublisher() {
        return queuePublisher;
    }

    @Override
    public final void close() {
        if (running.compareAndSet(true, false)) {
            onStop();
        }
    }

    protected <T> void processDelivery(QueueListener<T> listener, byte[] body) {
        if (LOG.isTraceEnabled()) {
            final String rawPayload = new String(body, StandardCharsets.UTF_8);
            LOG.trace("Raw bytes received from queue '{}': {}", listener.getQueueName(),
                rawPayload);
        }
        final Class<T> targetType = listener.getMessageType();
        final T payload = serializerRegistry.get(listener.getFormat())
            .deserializeFromBytes(body, targetType);
        LOG.debug("Processing message from queue '{}': {}", listener.getQueueName(), payload);
        listener.onMessage(payload);
    }

    protected abstract void onStart() throws Exception;

    protected abstract void onStop();

    protected abstract void onPublish(String exchange, String routingKey, byte[] body)
        throws Exception;

    protected abstract void onRegisterListener(QueueListener<?> listener) throws IOException;

    private void registerListeners(Set<QueueListener<?>> listeners) {
        int registeredListeners = 0;
        for (final QueueListener<?> listener : listeners) {
            final String queueName = listener.getQueueName();
            try {
                onRegisterListener(listener);
                LOG.info("Registered listener for queue: {} (format: {})", queueName,
                    listener.getFormat());
                registeredListeners++;
            } catch (Exception ex) {
                throw new CriticalBootstrapException("Failed to register listener for queue: " +
                    listener.getQueueName(), ex);
            }
        }
        LOG.info("Registered {} queue listener(s)", registeredListeners);
    }

    protected static abstract class AbstractBuilder<B extends AbstractBuilder<B>> {
        protected String username;
        protected String password;
        protected Set<HostPort> nodes = new HashSet<>();
        protected SerializerRegistry serializerRegistry;
        protected ComponentProvider componentProvider;

        protected AbstractBuilder() {
        }

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B nodes(Set<HostPort> nodes) {
            this.nodes = nodes;
            return self();
        }

        // as host:port
        public B rawNodes(Set<String> rawNodes) {
            return nodes(rawNodes.stream()
                .map(NetworkUtil::parseHostPort)
                .map(hp -> new HostPort(hp.host(), hp.port()))
                .collect(Collectors.toSet()));
        }

        public B username(String username) {
            this.username = username;
            return self();
        }

        public B password(String password) {
            this.password = password;
            return self();
        }

        public B serializerRegistry(SerializerRegistry serializerRegistry) {
            this.serializerRegistry = serializerRegistry;
            return self();
        }

        public B componentProvider(ComponentProvider componentProvider) {
            this.componentProvider = componentProvider;
            return self();
        }

        protected void validate() {
            Assert.notNull(nodes, "Nodes cannot be null");
            Assert.notNull(username, "Username cannot be null");
            Assert.notNull(password, "Password cannot be null");
            Assert.notNull(serializerRegistry, "SerializerRegistry cannot be null");
            Assert.notNull(componentProvider, "ComponentProvider cannot be null");
        }

        public abstract QueueServer build();
    }
}
