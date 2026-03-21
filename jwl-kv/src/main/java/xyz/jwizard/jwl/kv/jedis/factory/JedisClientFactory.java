package xyz.jwizard.jwl.kv.jedis.factory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

import java.util.Set;

public interface JedisClientFactory {
    UnifiedJedis create(Set<HostAndPort> nodes, JedisClientConfig config);

    FactoryType type();
}
