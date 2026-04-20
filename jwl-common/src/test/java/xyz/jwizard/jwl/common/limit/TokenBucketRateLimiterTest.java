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
package xyz.jwizard.jwl.common.limit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenBucketRateLimiterTest {
    private static final long CAPACITY = 5;
    private static final long REFILL_TOKENS = 5;
    private static final Duration REFILL_PERIOD = Duration.ofMillis(200);

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = TokenBucketRateLimiter.builder()
            .capacity(CAPACITY)
            .refillTokens(REFILL_TOKENS)
            .refillPeriod(REFILL_PERIOD)
            .build();
    }

    @Test
    @DisplayName("should allow requests up to configured capacity")
    void shouldAllowRequestsUpToCapacity() {
        // given
        final String key = "user-1";
        // when & then
        for (int i = 0; i < CAPACITY; i++) {
            assertTrue(rateLimiter.tryAcquire(key), "Should acquire token for request " + (i + 1));
        }
    }

    @Test
    @DisplayName("should reject requests when capacity is exceeded")
    void shouldRejectRequestsWhenCapacityExceeded() {
        // given
        final String key = "user-2";
        // when
        for (int i = 0; i < CAPACITY; i++) {
            rateLimiter.tryAcquire(key);
        }
        // then
        assertFalse(rateLimiter.tryAcquire(key), "Should reject request after exceeding capacity");
    }

    @Test
    @DisplayName("should isolate requests from different keys")
    void shouldIsolateDifferentKeys() {
        // given
        final String userA = "user-A";
        final String userB = "user-B";
        // when
        for (int i = 0; i < CAPACITY; i++) {
            rateLimiter.tryAcquire(userA);
        }
        // then
        assertFalse(rateLimiter.tryAcquire(userA), "User A should be rate limited");
        assertTrue(rateLimiter.tryAcquire(userB),
            "User B should not be affected by User A's limit");
    }

    @Test
    @DisplayName("should reset bucket capacity for a given key")
    void shouldResetBucketForKey() {
        // given
        final String key = "user-3";
        for (int i = 0; i < CAPACITY; i++) {
            rateLimiter.tryAcquire(key);
        }
        assertFalse(rateLimiter.tryAcquire(key));
        // when
        rateLimiter.reset(key);
        // then
        assertTrue(rateLimiter.tryAcquire(key),
            "Should acquire token successfully after bucket reset");
    }

    @Test
    @DisplayName("should refill tokens over time")
    void shouldRefillTokensOverTime() throws InterruptedException {
        // given
        final String key = "user-4";
        for (int i = 0; i < CAPACITY; i++) {
            rateLimiter.tryAcquire(key);
        }
        assertFalse(rateLimiter.tryAcquire(key));
        // when
        Thread.sleep(REFILL_PERIOD.toMillis() + 50);
        // then
        assertTrue(rateLimiter.tryAcquire(key),
            "Should acquire token after refill period has passed");
    }
}
