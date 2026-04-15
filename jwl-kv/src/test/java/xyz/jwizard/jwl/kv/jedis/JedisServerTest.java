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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.kv.jedis.factory.JedisClientFactory;
import xyz.jwizard.jwl.kv.key.TestKvKey;

import redis.clients.jedis.RedisClusterClient;
import redis.clients.jedis.params.SetParams;

@ExtendWith(MockitoExtension.class)
class JedisServerTest {
    @Mock
    private JedisClientFactory factory;

    @Mock
    private RedisClusterClient redisClient;

    private JedisServer jedisServer;

    @BeforeEach
    void setup() {
        // given
        Mockito.when(factory.create(any(), any())).thenReturn(redisClient);
        jedisServer = JedisServer.builder()
            .nodes(Set.of(new HostPort("localhost", 6379)))
            .withFactory(factory)
            .build();
        jedisServer.start();
    }

    @Test
    @DisplayName("should correctly format key using enum and delegate to underlying client")
    void shouldCorrectlyBuildKeyFromEnumAndCallSet() {
        // given
        final String value = "JWizard";
        final int param = 123;
        final TestKvKey key = TestKvKey.USER_PROFILE;
        // when
        jedisServer.set(key, value, param);
        // then
        verify(redisClient).set(TestKvKey.USER_PROFILE.build(param), value);
    }

    @Test
    @DisplayName("should append SetParams with EX flag when saving key with Time-To-Live")
    void shouldUseSetParamsWhenEnumHasTtl() {
        // given
        final String value = "some-value";
        final TestKvKey key = TestKvKey.TEMP_SESSION;
        // when
        jedisServer.setWithTtl(key, value);
        // then
        verify(redisClient).set(
            eq(TestKvKey.TEMP_SESSION.build()),
            eq(value),
            any(SetParams.class)
        );
    }
}
