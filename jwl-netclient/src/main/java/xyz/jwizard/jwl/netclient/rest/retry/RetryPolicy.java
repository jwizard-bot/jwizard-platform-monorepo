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
package xyz.jwizard.jwl.netclient.rest.retry;

import java.time.Duration;
import java.util.Set;

import xyz.jwizard.jwl.common.retry.RetryPolicyContext;
import xyz.jwizard.jwl.net.http.HttpMethod;

public class RetryPolicy implements RetryPolicyContext<HttpMethod> {
    private static final long MAX_BACKOFF_MS = 30_000L;
    private static final Set<HttpMethod> SAFE_METHODS = Set.of(
        HttpMethod.GET,
        HttpMethod.HEAD,
        HttpMethod.OPTIONS,
        HttpMethod.TRACE,
        HttpMethod.PUT,
        HttpMethod.DELETE
    );

    private final int maxAttempts;
    private final long backoffMs;
    private final long maxBackoffMs;
    private final Set<HttpMethod> allowedMethods;

    private RetryPolicy(int maxAttempts, long backoffMs, long maxBackoffMs,
                        Set<HttpMethod> allowedMethods) {
        this.maxAttempts = maxAttempts;
        this.backoffMs = backoffMs;
        this.maxBackoffMs = maxBackoffMs;
        this.allowedMethods = allowedMethods;
    }

    public static RetryPolicy withSafeMethods(int maxAttempts, Duration backoff,
                                              Duration maxBackoff) {
        return new RetryPolicy(maxAttempts, backoff.toMillis(), maxBackoff.toMillis(),
            SAFE_METHODS);
    }

    public static RetryPolicy withSafeMethods(int maxAttempts, Duration backoff) {
        return new RetryPolicy(maxAttempts, backoff.toMillis(), MAX_BACKOFF_MS, SAFE_METHODS);
    }

    public static RetryPolicy with(int maxAttempts, Duration backoff, Duration maxBackoff,
                                   HttpMethod... allowedMethods) {
        return new RetryPolicy(maxAttempts, backoff.toMillis(), maxBackoff.toMillis(),
            Set.of(allowedMethods));
    }

    public static RetryPolicy with(int maxAttempts, Duration backoff, HttpMethod... allowedMethods) {
        return new RetryPolicy(maxAttempts, backoff.toMillis(), MAX_BACKOFF_MS,
            Set.of(allowedMethods));
    }

    public static RetryPolicy none() {
        return new RetryPolicy(0, 0, 0, Set.of());
    }

    @Override
    public boolean shouldRetry(int currentAttempt, HttpMethod method) {
        return currentAttempt < maxAttempts && allowedMethods.contains(method);
    }

    @Override
    public boolean isEnabled() {
        return maxAttempts > 0;
    }

    @Override
    public long getBackoffMs() {
        return backoffMs;
    }

    @Override
    public long getMaxBackoffMs() {
        return maxBackoffMs;
    }
}
