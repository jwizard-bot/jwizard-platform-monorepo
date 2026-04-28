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
package xyz.jwizard.jwl.common.util.math;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MathUtilTest {
    @Test
    @DisplayName("should return zero if base delay is zero or negative")
    void shouldReturnZeroForInvalidBaseDelay() {
        // given
        final long zeroBase = 0;
        final long negativeBase = -50;
        // when
        final long resultZero = MathUtil.calcExpBackoff(1, zeroBase, 1000);
        final long resultNegative = MathUtil.calcExpBackoff(1, negativeBase, 1000);
        // then
        assertThat(resultZero).isZero();
        assertThat(resultNegative).isZero();
    }

    @ParameterizedTest
    @CsvSource({
        "1, 100, 90, 110",    // 100 * 2^0 = 100 (+/- 10%)
        "2, 100, 180, 220",   // 100 * 2^1 = 200 (+/- 10%)
        "3, 100, 360, 440",   // 100 * 2^2 = 400 (+/- 10%)
        "4, 100, 720, 880"    // 100 * 2^3 = 800 (+/- 10%)
    })
    @DisplayName("should calculate exponential delay within jitter range")
    void shouldCalculateExpDelayWithJitter(int attempt, long baseDelay, long min, long max) {
        // given: csv
        // when
        final long delay = MathUtil.calcExpBackoff(attempt, baseDelay, 10000);
        // then
        assertThat(delay).isBetween(min, max);
    }

    @Test
    @DisplayName("should cap the delay at maxDelayMs")
    void shouldCapMaxDelay() {
        // given
        final int highAttempt = 20;
        final long baseDelay = 100;
        final long maxLimit = 1000;
        // when
        final long delay = MathUtil.calcExpBackoff(highAttempt, baseDelay, maxLimit);
        // then
        assertThat(delay).isEqualTo(maxLimit);
    }

    @Test
    @DisplayName("should treat negative or zero attempt as attempt 1")
    void shouldHandleInvalidAttempts() {
        // given
        final long baseDelay = 100;
        final long maxDelay = 1000;
        // when
        final long delayZero = MathUtil.calcExpBackoff(0, baseDelay, maxDelay);
        final long delayNegative = MathUtil.calcExpBackoff(-5, baseDelay, maxDelay);
        // then
        assertThat(delayZero).isBetween(90L, 110L);
        assertThat(delayNegative).isBetween(90L, 110L);
    }
}
