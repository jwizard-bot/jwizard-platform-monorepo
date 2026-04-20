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
package xyz.jwizard.jwl.kv.jedis.pubsub.pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexChannelParamExtractorTest {
    @Test
    @DisplayName("should return empty array when pattern has no wildcards")
    void shouldHandleExactMatch() {
        // given
        final RegexChannelParamExtractor extractor = new RegexChannelParamExtractor(
            "sys:global:events"
        );
        // when
        final String[] params = extractor.extract("sys:global:events");
        // then
        assertEquals(0, params.length);
    }

    @Test
    @DisplayName("should extract single parameter from wildcard")
    void shouldExtractSingleWildcard() {
        // given
        final RegexChannelParamExtractor extractor = new RegexChannelParamExtractor(
            "user:*:notifications"
        );
        // when
        final String[] params = extractor.extract("user:jwizard_123:notifications");
        // then
        assertEquals(1, params.length);
        assertEquals("jwizard_123", params[0]);
    }

    @Test
    @DisplayName("should extract multiple parameters from multiple wildcards")
    void shouldExtractMultipleWildcards() {
        // given
        final RegexChannelParamExtractor extractor = new RegexChannelParamExtractor(
            "game:*:match:*:player:*:stats"
        );
        // when
        final String[] params = extractor.extract("game:lol:match:999:player:faker:stats");
        // then
        assertEquals(3, params.length);
        assertArrayEquals(new String[]{"lol", "999", "faker"}, params);
    }

    @Test
    @DisplayName("should return empty array when channel does not match pattern")
    void shouldReturnEmptyWhenNoMatch() {
        // given
        final RegexChannelParamExtractor extractor = new RegexChannelParamExtractor(
            "user:*:notifications"
        );
        // when
        final String[] params = extractor.extract("user:123:other_events");
        // then
        assertEquals(0, params.length);
    }

    @Test
    @DisplayName("should handle nulls gracefully")
    void shouldHandleNulls() {
        // given
        final RegexChannelParamExtractor extractor = new RegexChannelParamExtractor(null);
        // when & then
        assertEquals(0, extractor.extract("some:channel").length);
        assertEquals(0, extractor.extract(null).length);
    }
}
