package xyz.jwizard.jwl.kv.jedis;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.SetParams;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.common.util.thread.ThreadUtil;
import xyz.jwizard.jwl.kv.KvClusterNode;
import xyz.jwizard.jwl.kv.KvServer;
import xyz.jwizard.jwl.kv.jedis.factory.ClusterJedisClientFactory;
import xyz.jwizard.jwl.kv.jedis.factory.FactoryType;
import xyz.jwizard.jwl.kv.jedis.factory.JedisClientFactory;
import xyz.jwizard.jwl.kv.jedis.pubsub.BinaryJedisPubSubAdapter;
import xyz.jwizard.jwl.kv.jedis.pubsub.JedisPubSubAdapter;
import xyz.jwizard.jwl.kv.key.KvChannel;
import xyz.jwizard.jwl.kv.key.KvKey;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JedisServer extends KvServer {
    private final JedisClientFactory clientFactory;
    private UnifiedJedis redisClient;

    protected JedisServer(Set<KvClusterNode> kvClusterNodes, String password,
                          JedisClientFactory clientFactory) {
        super(kvClusterNodes, password);
        this.clientFactory = clientFactory;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void onStart() {
        if (redisClient != null) {
            LOG.warn("KV server adapter start() ignored: client is already running");
            return;
        }
        final JedisClientConfig config = DefaultJedisClientConfig.builder()
            .password(password != null && !password.isBlank() ? password : null)
            .build();

        final Set<HostAndPort> clusterNodes = kvClusterNodes.stream()
            .map(c -> new HostAndPort(c.host(), c.port()))
            .collect(Collectors.toSet());

        redisClient = clientFactory.create(clusterNodes, config);
        final String pingResponse = redisClient.ping();
        LOG.debug("KV server ping response: {}", pingResponse);
        LOG.info("Successfully connected to KV server, mode: {}", clientFactory.type());
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(redisClient, client -> {
            client.close();
            redisClient = null;
            LOG.info("KV server cluster connections closed");
        });
    }

    @Override
    public void set(KvKey key, String value, Object... keyParams) {
        final String exactKey = key.build(keyParams);
        LOG.debug("KV SET -> key: '{}'", exactKey);
        redisClient.set(exactKey, value);
    }

    @Override
    public void setWithTtl(KvKey key, String value, Object... keyParams) {
        final String exactKey = key.build(keyParams);
        final long ttl = key.getDefaultTtlSeconds();
        if (ttl > 0) {
            LOG.debug("KV SETEX -> key: '{}', TTL: {}s", exactKey, ttl);
            redisClient.set(exactKey, value, SetParams.setParams().ex(ttl));
        } else {
            set(key, value, keyParams);
        }
    }

    @Override
    public String get(KvKey key, Object... keyParams) {
        final String exactKey = key.build(keyParams);
        final String value = redisClient.get(exactKey);
        LOG.debug("KV GET -> key: '{}', found: {}", exactKey, value != null);
        return value;
    }

    @Override
    public void del(KvKey key, Object... keyParams) {
        final String exactKey = key.build(keyParams);
        LOG.debug("KV DEL -> key: '{}'", exactKey);
        redisClient.del(exactKey);
    }

    @Override
    public void publish(KvChannel channel, String message, Object... channelParams) {
        final String exactChannelName = channel.buildChannel(channelParams);
        LOG.debug("KV PUBLISH -> channel: '{}'", exactChannelName);
        redisClient.publish(exactChannelName, message);
    }

    @Override
    public void publishBinary(KvChannel channel, byte[] message, Object... channelParams) {
        final String exactChannelName = channel.buildChannel(channelParams);
        final byte[] channelBytes = exactChannelName.getBytes(StandardCharsets.UTF_8);
        LOG.debug("KV PUBLISH (binary) -> channel: '{}'", exactChannelName);
        redisClient.publish(channelBytes, message);
    }

    @Override
    public void subscribe(KvChannel channel, Consumer<String> onMessage, Object... channelParams) {
        final String channelName = channel.buildChannel(channelParams);
        final JedisPubSubAdapter pubSub = new JedisPubSubAdapter(onMessage);

        LOG.info("Registering Pub/Sub listener on channel: '{}'", channelName);
        ThreadUtil.runAsync("kv-sub-" + channelName, () ->
            redisClient.subscribe(pubSub, channelName));
    }

    @Override
    public void subscribeBinary(KvChannel channel, Consumer<byte[]> onMessage,
                                Object... channelParams) {
        final String channelName = channel.buildChannel(channelParams);
        final byte[] channelBytes = channelName.getBytes(StandardCharsets.UTF_8);
        final BinaryJedisPubSubAdapter pubSub = new BinaryJedisPubSubAdapter(onMessage);

        LOG.info("Registering Binary Pub/Sub listener on channel: '{}'", channelName);
        ThreadUtil.runAsync("kv-sub-bin-" + channelName, () ->
            redisClient.subscribe(pubSub, channelBytes));
    }

    public static class Builder extends KvServer.Builder<Builder, JedisServer> {
        private JedisClientFactory factory = new ClusterJedisClientFactory();

        public Builder withFactory(FactoryType factoryType) {
            this.factory = factoryType.getFactory();
            return this;
        }

        public Builder withFactory(JedisClientFactory factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public JedisServer build() {
            return new JedisServer(nodes, password, factory);
        }
    }
}
