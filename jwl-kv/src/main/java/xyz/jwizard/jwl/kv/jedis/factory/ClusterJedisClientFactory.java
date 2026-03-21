package xyz.jwizard.jwl.kv.jedis.factory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.RedisClusterClient;
import redis.clients.jedis.UnifiedJedis;

import java.util.Set;

public class ClusterJedisClientFactory implements JedisClientFactory {
    @Override
    public UnifiedJedis create(Set<HostAndPort> nodes, JedisClientConfig config) {
        return RedisClusterClient.builder()
            .nodes(nodes)
            .clientConfig(config)
            .build();
    }

    @Override
    public FactoryType type() {
        return FactoryType.CLUSTER;
    }
}
