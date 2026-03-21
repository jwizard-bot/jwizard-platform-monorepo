package xyz.jwizard.jwl.kv.jedis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.function.Consumer;

public class BinaryJedisPubSubAdapter extends BinaryJedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryJedisPubSubAdapter.class);

    private final Consumer<byte[]> onMessageConsumer;

    public BinaryJedisPubSubAdapter(Consumer<byte[]> onMessageConsumer) {
        this.onMessageConsumer = onMessageConsumer;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        LOG.debug("KV RECEIVED (PubSub) -> channel: '{}'", channel);
        if (onMessageConsumer != null) {
            onMessageConsumer.accept(message);
        }
    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        LOG.info("Successfully subscribed to channel: '{}' (total active: {})", channel,
            subscribedChannels);
    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        LOG.info("Unsubscribed from channel: '{}'", channel);
    }
}
