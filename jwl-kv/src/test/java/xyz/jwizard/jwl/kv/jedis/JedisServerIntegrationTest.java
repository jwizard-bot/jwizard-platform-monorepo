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

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.kv.jedis.factory.FactoryType;
import xyz.jwizard.jwl.kv.key.KvChannel;
import xyz.jwizard.jwl.kv.key.KvKey;
import xyz.jwizard.jwl.kv.key.TestKvChannel;
import xyz.jwizard.jwl.kv.key.TestKvKey;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JedisServerIntegrationTest {
    private static final int REDIS_PORT = 6379;

    @Container
    static RedisContainer redisContainer = new RedisContainer(
        DockerImageName.parse("redis:7.2-alpine")
    ).withExposedPorts(REDIS_PORT);

    private JedisServer jedisServer;

    @BeforeEach
    void setup() {
        final String host = redisContainer.getHost();
        final int port = redisContainer.getMappedPort(REDIS_PORT);
        jedisServer = JedisServer.builder()
            .nodes(Set.of(new HostPort(host, port)))
            .withFactory(FactoryType.SINGLE_NODE)
            .build();
        jedisServer.start();
    }

    @AfterEach
    void tearDown() {
        IoUtil.closeQuietly(jedisServer);
    }

    @Test
    @DisplayName("should successfully save and retrieve data from real Redis instance")
    void shouldPerformRealSetAndGetOperations() {
        // given
        final KvKey key = TestKvKey.USER_PROFILE;
        String userId = "999";
        String expectedValue = "JWizard_Admin";
        // when
        jedisServer.set(key, expectedValue, userId);
        String actualValue = jedisServer.get(key, userId);
        // then
        assertEquals(expectedValue, actualValue,
            "Value retrieved from Redis should match the one we set.");
    }

    @Test
    @DisplayName("should automatically remove key after specified TTL expires")
    void shouldHandleExpirationWithTtl() throws InterruptedException {
        // given
        final KvKey key = TestKvKey.TEMP_SESSION;
        final long ttl = TestKvKey.TEMP_SESSION.getDefaultTtlSeconds();
        // when
        jedisServer.setWithTtl(key, "temporary-data");
        // then
        assertEquals("temporary-data", jedisServer.get(key));
        Thread.sleep((ttl * 2) * 1000 + ttl);
        assertNull(jedisServer.get(key), "Key should have expired and been removed.");
    }

    @Test
    @DisplayName("should successfully delete a specific key from Redis")
    void shouldDeleteExistingKeySuccessfully() {
        // given
        final KvKey key = TestKvKey.USER_PROFILE;
        final String userId = "delete_user_777";
        final String expectedValue = "Data_to_be_deleted";
        jedisServer.set(key, expectedValue, userId);
        final String valueBeforeDeletion = jedisServer.get(key, userId);
        assertNotNull(valueBeforeDeletion, "Key should exist before deletion.");
        assertEquals(expectedValue, valueBeforeDeletion,
            "Retrieved value should match what was set.");
        // when
        jedisServer.del(key, userId);
        // then
        final String valueAfterDeletion = jedisServer.get(key, userId);
        assertNull(valueAfterDeletion, "Key should return null after being deleted.");
    }

    @Test
    @DisplayName("should successfully broadcast and receive message via Pub/Sub channel")
    void shouldPublishAndReceiveMessage() throws InterruptedException {
        // given
        final KvChannel channel = TestKvChannel.TEST_EVENTS;
        final String expectedMessage = "Hello_PubSub!";
        final CountDownLatch messageReceivedLatch = new CountDownLatch(1);
        final AtomicReference<String> receivedMessageRef = new AtomicReference<>();
        // when
        jedisServer.subscribe(channel, message -> {
            receivedMessageRef.set(message);
            messageReceivedLatch.countDown();
        });
        Thread.sleep(500);
        // when
        jedisServer.publish(channel, expectedMessage);
        final boolean messageArrived = messageReceivedLatch.await(3, TimeUnit.SECONDS);
        assertTrue(messageArrived, "Did not receive Pub/Sub message within timeout.");
        assertEquals(expectedMessage, receivedMessageRef.get(),
            "Received message does not match published message.");
    }

    @Test
    @DisplayName("should successfully broadcast and receive params message via Pub/Sub channel")
    void shouldPublishAndReceiveParameterizedMessage() throws InterruptedException {
        // given
        final KvChannel channel = TestKvChannel.USER_NOTIFICATIONS;
        final String userId = "jwizard_123";
        final String expectedMessage = "You have a new alert!";
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> receivedRef = new AtomicReference<>();
        // when
        jedisServer.subscribe(channel, message -> {
            receivedRef.set(message);
            latch.countDown();
        }, userId);
        Thread.sleep(500);
        jedisServer.publish(channel, expectedMessage, userId);
        // then
        final boolean messageArrived = latch.await(3, TimeUnit.SECONDS);
        assertTrue(messageArrived, "Did not receive parameterized Pub/Sub message within timeout.");
        assertEquals(expectedMessage, receivedRef.get(),
            "Received message does not match published message.");
    }

    @Test
    @DisplayName("should successfully broadcast and receive binary message via Pub/Sub channel")
    void shouldPublishAndReceiveBinaryMessage() throws InterruptedException {
        // given
        final KvChannel channel = TestKvChannel.TEST_EVENTS;
        final byte[] expectedPayload = "Binary_Payload_Mock_Data"
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<byte[]> receivedRef = new AtomicReference<>();
        // when
        jedisServer.subscribeBinary(channel, messageBytes -> {
            receivedRef.set(messageBytes);
            latch.countDown();
        });
        Thread.sleep(500);
        jedisServer.publishBinary(channel, expectedPayload);
        // then
        final boolean messageArrived = latch.await(3, TimeUnit.SECONDS);
        assertTrue(messageArrived, "Did not receive binary Pub/Sub message within timeout.");
        assertNotNull(receivedRef.get(), "Received binary message should not be null.");
        assertArrayEquals(expectedPayload, receivedRef.get(),
            "Received binary payload does not match the published payload.");
    }
}
