package xyz.jwizard.jwl.queue;

import java.util.concurrent.CountDownLatch;

public class JsonCommandListener implements QueueListener<PlayTrackCommand> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private PlayTrackCommand receivedCommand;

    @Override
    public String getQueueName() {
        return "test.json.queue";
    }

    @Override
    public Class<PlayTrackCommand> getMessageType() {
        return PlayTrackCommand.class;
    }

    @Override
    public void onMessage(PlayTrackCommand message) {
        this.receivedCommand = message;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public PlayTrackCommand getReceivedCommand() {
        return receivedCommand;
    }
}
