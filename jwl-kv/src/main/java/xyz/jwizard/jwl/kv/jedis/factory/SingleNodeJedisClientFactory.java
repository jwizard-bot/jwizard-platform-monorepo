package xyz.jwizard.jwl.kv.jedis.factory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.UnifiedJedis;

import java.util.Set;

public class SingleNodeJedisClientFactory implements JedisClientFactory {
    @Override
    public UnifiedJedis create(Set<HostAndPort> nodes, JedisClientConfig config) {
        final HostAndPort singleNode = nodes.iterator().next();
        return RedisClient.builder()
            .hostAndPort(singleNode.getHost(), singleNode.getPort())
            .build();
    }

    @Override
    public FactoryType type() {
        return FactoryType.SINGLE_NODE;
    }
}
