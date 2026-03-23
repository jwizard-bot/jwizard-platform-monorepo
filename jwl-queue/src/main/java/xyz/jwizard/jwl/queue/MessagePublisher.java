package xyz.jwizard.jwl.queue;

import xyz.jwizard.jwl.common.serialization.SerializerFormat;

public interface MessagePublisher {
    <T> void publish(String exchange, String routingKey, T payload);

    <T> void publish(String exchange, String routingKey, T payload, SerializerFormat format);

    <T> void publishToQueue(String queueName, T payload);

    <T> void publishToQueue(String queueName, T payload, SerializerFormat format);
}
