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
package xyz.jwizard.jws.gateway;

import java.util.Set;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.serialization.json.JacksonSerializer;
import xyz.jwizard.jwl.common.serialization.raw.RawByteSerializer;
import xyz.jwizard.jwl.queue.MessagePublisher;
import xyz.jwizard.jwl.queue.QueueServer;
import xyz.jwizard.jwl.queue.rabbitmq.RabbitMqServer;
import xyz.jwizard.jwl.queue.rabbitmq.connector.ConnectorType;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class QueueServerLifecycle implements LifecycleHook {
    private final QueueServer queueServer;

    @Inject
    QueueServerLifecycle(ComponentProvider componentProvider) {
        queueServer = RabbitMqServer.builder()
            .rawNodes(Set.of("localhost:9111") /*TODO: incoming from config server*/)
            .withConnector(ConnectorType.SINGLE_NODE)
            .username("guest" /*TODO: incoming from config server*/)
            .password("guest" /*TODO: incoming from config server*/)
            .virtualHost("jwizard-main" /*TODO: incoming from config server*/)
            .serializerRegistry(SerializerRegistry.createDefault()
                .register(JacksonSerializer.createLenientForMessaging())
                .register(RawByteSerializer.createDefault())
            )
            .componentProvider(componentProvider)
            .build();
    }

    @Override
    public void onStart(ComponentProvider componentProvider, ClassScanner scanner) {
        queueServer.start();
    }

    @Override
    public void onStop() {
        queueServer.close();
    }

    @Produces
    MessagePublisher messagePublisher() {
        return queueServer.getQueuePublisher();
    }
}
