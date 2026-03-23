package xyz.jwizard.jwl.queue;

import xyz.jwizard.jwl.common.serialization.SerializerFormat;

public class FailingListener implements QueueListener<byte[]> {
    @Override
    public String getQueueName() {
        return "test.fail.queue";
    }

    @Override
    public Class<byte[]> getMessageType() {
        return byte[].class;
    }

    @Override
    public SerializerFormat getFormat() {
        return SerializerFormat.RAW;
    }

    @Override
    public QueueTopology getTopology() {
        return QueueTopology.builder()
            .withDeadLetter()
            .build();
    }

    @Override
    public void onMessage(byte[] message) {
        throw new RuntimeException("Simulated processing failure");
    }
}
