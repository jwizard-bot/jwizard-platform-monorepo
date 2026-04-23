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

import xyz.jwizard.jwl.common.serialization.SerializerFormat;
import xyz.jwizard.jwl.common.serialization.StandardSerializerFormat;

public class QueuePublisher implements MessagePublisher {
    private static final Logger LOG = LoggerFactory.getLogger(QueuePublisher.class);

    private final QueueServer queueServer;

    QueuePublisher(QueueServer queueServer) {
        this.queueServer = queueServer;
    }

    @Override
    public <T> void publish(String exchange, String routingKey, T payload) {
        publish(exchange, routingKey, payload, StandardSerializerFormat.JSON);
    }

    @Override
    public <T> void publish(String exchange, String routingKey, T payload,
                            SerializerFormat format) {
        final String logExchange = (exchange == null || exchange.isBlank())
            ? "<default>"
            : exchange;
        try {
            LOG.trace("Publishing message to exchange '{}' with routing key '{}'", logExchange,
                routingKey);
            final byte[] body = queueServer.getSerializerRegistry()
                .get(format)
                .serializeToBytes(payload);
            queueServer.onPublish(exchange, routingKey, body);
        } catch (Exception ex) {
            LOG.error("Failed to publish message to exchange '{}' with routing key '{}'",
                logExchange, routingKey, ex);
        }
    }

    @Override
    public <T> void publishToQueue(String queueName, T payload) {
        publish("", queueName, payload);
    }

    @Override
    public <T> void publishToQueue(String queueName, T payload, SerializerFormat format) {
        publish("", queueName, payload, format);
    }
}
