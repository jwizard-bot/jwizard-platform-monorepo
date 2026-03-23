package xyz.jwizard.jwl.queue;

import xyz.jwizard.jwl.common.serialization.SerializerFormat;

import java.util.concurrent.CountDownLatch;

public class HappyPathListener implements QueueListener<byte[]> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private byte[] receivedMessage;

    @Override
    public String getQueueName() {
        return "test.happy.queue";
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
    public void onMessage(byte[] message) {
        this.receivedMessage = message;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public byte[] getReceivedMessage() {
        return receivedMessage;
    }
}
