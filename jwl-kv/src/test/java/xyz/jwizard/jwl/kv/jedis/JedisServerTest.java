package xyz.jwizard.jwl.kv.jedis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.RedisClusterClient;
import redis.clients.jedis.params.SetParams;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.kv.jedis.factory.JedisClientFactory;
import xyz.jwizard.jwl.kv.key.TestKvKey;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

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
