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
package xyz.jwizard.jwl.netclient.rest.intercept;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.common.limit.RateLimiter;
import xyz.jwizard.jwl.net.http.HttpStatus;
import xyz.jwizard.jwl.netclient.rest.RestResponse;

public class RateLimitInterceptor implements RequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private final RateLimiter rateLimiter;
    private final int order;

    public RateLimitInterceptor(RateLimiter rateLimiter, int order) {
        this.rateLimiter = rateLimiter;
        this.order = order;
    }

    public RateLimitInterceptor(RateLimiter rateLimiter) {
        this(rateLimiter, Integer.MAX_VALUE);
    }

    @Override
    public void intercept(InterceptorContext context) {
        final RequestView view = context.getView();
        final String poolName = view.getPool().getPoolName();

        LOG.trace("Intercepting request for pool: {} (Method: {}, Path: {})", poolName,
            view.getMethod(), view.getUriPath());

        if (!rateLimiter.tryAcquire(poolName)) {
            LOG.info("Rate limit exceeded for pool: {}, aborting request: {} {}", poolName,
                view.getMethod(), view.getUriPath());
            final RestResponse<Void> tooManyRequestsResponse = new RestResponse<>(
                HttpStatus.TOO_MANY_REQUESTS_429.getCode(),
                Collections.emptyMap(),
                null
            );
            context.abortWith(tooManyRequestsResponse);
        }
        LOG.debug("Rate limit permit acquired for pool: {}", poolName);
    }

    @Override
    public int order() {
        return order;
    }
}
