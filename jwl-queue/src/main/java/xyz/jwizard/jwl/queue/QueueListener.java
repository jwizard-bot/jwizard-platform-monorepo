package xyz.jwizard.jwl.queue;

import xyz.jwizard.jwl.common.serialization.SerializerFormat;

public interface QueueListener<T> {
    String getQueueName();

    Class<T> getMessageType();

    void onMessage(T message);

    default SerializerFormat getFormat() {
        return SerializerFormat.JSON;
    }

    default QueueTopology getTopology() {
        return QueueTopology.builder().build();
    }
}
