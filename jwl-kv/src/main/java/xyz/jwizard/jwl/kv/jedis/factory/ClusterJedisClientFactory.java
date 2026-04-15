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
package xyz.jwizard.jwl.kv.jedis.factory;

import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.RedisClusterClient;
import redis.clients.jedis.UnifiedJedis;

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
