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

import java.util.concurrent.Callable;
import java.util.function.BiPredicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;
import xyz.jwizard.jwl.common.util.math.MathUtil;

public class RetryExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(RetryExecutor.class);

    private RetryExecutor() {
        throw new ForbiddenInstantiationException(RetryExecutor.class);
    }

    public static <T, C> T executeSync(Callable<T> action, C context, RetryPolicyContext<C> policy,
                                       BiPredicate<Integer, T> retryableRes,
                                       BiPredicate<Integer, Exception> retryableErr)
        throws Exception {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                T result = action.call();
                if (shouldRetryResult(result, attempt, context, policy, retryableRes)) {
                    performBackoff(attempt, policy);
                    continue;
                }
                return result;
            } catch (Exception ex) {
                if (shouldRetryException(ex, attempt, context, policy, retryableErr)) {
                    performBackoff(attempt, policy);
                    continue;
                }
                throw ex;
            }
        }
    }

    private static <T, C> boolean shouldRetryResult(T result, int attempt, C context,
                                                    RetryPolicyContext<C> policy,
                                                    BiPredicate<Integer, T> retryableRes) {
        if (!retryableRes.test(attempt, result)) {
            return false;
        }
        if (policy.shouldRetry(attempt, context)) {
            LOG.debug("Result-based retry triggered, attempt: {}, context: {}", attempt, context);
            return true;
        }
        LOG.debug("Result-based retry conditions met, but policy denied further attempts ({})",
            attempt);
        return false;
    }

    private static <C> boolean shouldRetryException(Exception ex, int attempt, C context,
                                                    RetryPolicyContext<C> policy,
                                                    BiPredicate<Integer, Exception> retryableErr) {
        if (!retryableErr.test(attempt, ex)) {
            return false;
        }
        if (policy.shouldRetry(attempt, context)) {
            LOG.info("Exception-based retry triggered, attempt: {}, error: '{}', context: {}",
                attempt, ex.getMessage(), context);
            return true;
        }
        LOG.warn("Retry policy exhausted or denied for exception: '{}' after {} attempts",
            ex.getMessage(), attempt);
        return false;
    }

    private static void performBackoff(int attempt, RetryPolicyContext<?> policy) {
        final long delay = MathUtil.calcExpBackoff(attempt, policy.getBackoffMs(),
            policy.getMaxBackoffMs());
        if (delay > 0) {
            LOG.trace("Performing backoff for attempt {}: {}ms", attempt, delay);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                LOG.warn("Retry backoff interrupted", ex);
                Thread.currentThread().interrupt();
            }
        }
    }
}
