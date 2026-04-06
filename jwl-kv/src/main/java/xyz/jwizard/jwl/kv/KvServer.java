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
package xyz.jwizard.jwl.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.common.util.net.NetworkUtil;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class KvServer implements KeyValueStore, PubSubBroadcaster, Closeable {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final Set<HostPort> nodes;
    protected final String password;

    private final AtomicBoolean running = new AtomicBoolean(false);

    protected KvServer(AbstractBuilder<?> builder) {
        nodes = builder.nodes;
        password = builder.password;
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
            LOG.info("KV server start initializing with {} node(s)", nodes.size());
            onStart();
        } catch (Exception ex) {
            throw new CriticalBootstrapException("KV server connection(s) failed", ex);
        }
    }

    @Override
    public final void close() {
        if (running.compareAndSet(true, false)) {
            onStop();
        }
    }

    protected abstract void onStart() throws Exception;

    protected abstract void onStop();

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>> {
        protected Set<HostPort> nodes = new HashSet<>();
        protected String password;

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

        // might be null
        public B password(String password) {
            this.password = password;
            return self();
        }

        protected void validate() {
            Assert.notNull(nodes, "Nodes cannot be null");
        }

        public abstract KvServer build();
    }
}
