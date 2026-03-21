package xyz.jwizard.jwl.kv;

import xyz.jwizard.jwl.kv.key.KvChannel;

import java.util.function.Consumer;

public interface PubSubBroadcaster {
    void publish(KvChannel channel, String message, Object... channelParams);

    void publishBinary(KvChannel channel, byte[] message, Object... channelParams);

    void subscribe(KvChannel channel, Consumer<String> onMessage, Object... channelParams);

    void subscribeBinary(KvChannel channel, Consumer<byte[]> onMessage, Object... channelParams);
}
