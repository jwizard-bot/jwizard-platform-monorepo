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

import java.util.Map;

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
        final String groupName = view.getGroup().getClientGroupName();

        LOG.trace("Intercepting request for group: {} (method: {}, path: {})", groupName,
            view.getMethod(), view.getUrl());

        if (!rateLimiter.tryAcquire(groupName)) {
            LOG.info("Rate limit exceeded for group: {}, aborting request: {} {}", groupName,
                view.getMethod(), view.getUrl());
            final RestResponse<Void> tooManyRequestsResponse = new RestResponse<>(
                HttpStatus.TOO_MANY_REQUESTS_429.getCode(),
                Map.of(),
                null
            );
            context.abortWith(tooManyRequestsResponse);
        }
        LOG.debug("Rate limit permit acquired for group: {}", groupName);
    }

    @Override
    public int order() {
        return order;
    }
}
