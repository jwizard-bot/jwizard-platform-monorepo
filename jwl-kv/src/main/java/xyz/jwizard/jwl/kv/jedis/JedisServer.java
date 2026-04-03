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
package xyz.jwizard.jwl.kv.jedis;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.SetParams;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.common.util.thread.ThreadUtil;
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

    private JedisServer(Builder builder) {
        super(builder);
        this.clientFactory = builder.factory;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void onStart() {
        final JedisClientConfig config = DefaultJedisClientConfig.builder()
            .password(password != null && !password.isBlank() ? password : null)
            .build();

        final Set<HostAndPort> clusterNodes = nodes.stream()
            .map(c -> new HostAndPort(c.host(), c.port()))
            .collect(Collectors.toSet());

        redisClient = clientFactory.create(clusterNodes, config);
        final String pingResponse = redisClient.ping();
        LOG.debug("KV server ping response: {}", pingResponse);
        LOG.info("Successfully connected to KV server, mode: {}", clientFactory.type());
    }

    @Override
    protected void onStop() {
        IoUtil.closeQuietly(redisClient, UnifiedJedis::close);
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

    public static class Builder extends KvServer.AbstractBuilder<Builder> {
        private JedisClientFactory factory = new ClusterJedisClientFactory();

        private Builder() {
        }

        public Builder withFactory(FactoryType factoryType) {
            factory = factoryType.getFactory();
            return this;
        }

        public Builder withFactory(JedisClientFactory factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public JedisServer build() {
            validate();
            Assert.notNull(factory, "JedisClientFactory cannot be null");
            return new JedisServer(this);
        }
    }
}
