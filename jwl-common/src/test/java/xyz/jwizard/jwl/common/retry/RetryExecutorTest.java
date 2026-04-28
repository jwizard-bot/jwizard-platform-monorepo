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
package xyz.jwizard.jwl.common.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetryExecutorTest {
    private final String context = "test-context";
    @Mock
    private RetryPolicyContext<String> policy;

    @BeforeEach
    void setUp() {
        lenient().when(policy.getBackoffMs()).thenReturn(0L);
        lenient().when(policy.getMaxBackoffMs()).thenReturn(0L);
    }

    @Test
    @DisplayName("should return result immediately on first success")
    void shouldReturnImmediately() throws Exception {
        // given
        final Callable<String> action = () -> "success";
        // when
        final String result = RetryExecutor.executeSync(action, context, policy,
            (at, res) -> false, (at, ex) -> false);
        // then
        assertThat(result).isEqualTo("success");
        verify(policy, times(0)).shouldRetry(anyInt(), eq(context));
    }

    @Test
    @DisplayName("should retry when result matches retryable predicate and then succeed")
    void shouldRetryOnResult() throws Exception {
        // given
        final AtomicInteger attempts = new AtomicInteger(0);
        final Callable<Integer> action = attempts::incrementAndGet;
        final BiPredicate<Integer, Integer> retryOnOne = (attempt, res) -> res == 1;
        when(policy.shouldRetry(eq(1), eq(context))).thenReturn(true);
        // when
        final Integer result = RetryExecutor
            .executeSync(action, context, policy, retryOnOne, (at, ex) -> false);
        // then
        assertThat(result).isEqualTo(2);
        assertThat(attempts.get()).isEqualTo(2);
        verify(policy).shouldRetry(1, context);
    }

    @Test
    @DisplayName("should retry when exception occurs and then succeed")
    void shouldRetryOnException() throws Exception {
        // given
        final AtomicInteger attempts = new AtomicInteger(0);
        final Callable<String> action = () -> {
            if (attempts.incrementAndGet() == 1) {
                throw new RuntimeException("temporary error");
            }
            return "recovered";
        };
        when(policy.shouldRetry(eq(1), eq(context))).thenReturn(true);
        final BiPredicate<Integer, Exception> retryOnRuntime = (at, ex) ->
            ex instanceof RuntimeException;
        // when
        final String result = RetryExecutor.executeSync(action, context, policy,
            (at, res) -> false, retryOnRuntime);
        // then
        assertThat(result).isEqualTo("recovered");
        assertThat(attempts.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("should throw exception when retry limit is reached")
    void shouldFailAfterExhaustingRetries() {
        // given
        final Callable<String> action = () -> {
            throw new RuntimeException("persistent error");
        };
        when(policy.shouldRetry(anyInt(), eq(context))).thenReturn(true);
        when(policy.shouldRetry(eq(3), eq(context))).thenReturn(false);
        final BiPredicate<Integer, Exception> retryAlways = (at, ex) -> true;
        // when & then
        assertThatThrownBy(() ->
            RetryExecutor.executeSync(action, context, policy, (at, res) -> false, retryAlways)
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("persistent error");
        verify(policy, times(3)).shouldRetry(anyInt(), eq(context));
    }

    @Test
    @DisplayName("should respect backoff parameters from policy")
    void shouldRespectBackoff() throws Exception {
        // given
        final AtomicInteger attempts = new AtomicInteger(0);
        final Callable<String> action = () -> {
            if (attempts.incrementAndGet() == 1) return "retry-me";
            return "done";
        };
        when(policy.getBackoffMs()).thenReturn(10L);
        when(policy.getMaxBackoffMs()).thenReturn(100L);
        when(policy.shouldRetry(eq(1), eq(context))).thenReturn(true);
        // when
        RetryExecutor.executeSync(action, context, policy, (at, res) -> res.equals("retry-me"),
            (at, ex) -> false);
        // then
        verify(policy, atLeastOnce()).getBackoffMs();
        verify(policy, atLeastOnce()).getMaxBackoffMs();
    }
}
