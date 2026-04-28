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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.common.limit.RateLimiter;
import xyz.jwizard.jwl.net.http.HttpStatus;
import xyz.jwizard.jwl.netclient.rest.pool.UrlPool;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {
    @Mock
    private RateLimiter rateLimiter;
    @Mock
    private InterceptorContext context;
    @Mock
    private RequestView view;
    @Mock
    private UrlPool pool;

    private RateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitInterceptor(rateLimiter);
        lenient().when(context.getView()).thenReturn(view);
        lenient().when(view.getPool()).thenReturn(pool);
        lenient().when(pool.getPoolName()).thenReturn("DISCORD_API");
    }

    @Test
    @DisplayName("should let request pass when rate limit is not exceeded")
    void shouldLetRequestPass() {
        // given
        when(rateLimiter.tryAcquire("DISCORD_API")).thenReturn(true);
        // when
        interceptor.intercept(context);
        // then
        verify(context, never()).abortWith(any());
    }

    @Test
    @DisplayName("should abort request with 429 when rate limit is exceeded")
    void shouldAbortWhenRateLimitExceeded() {
        // given
        when(rateLimiter.tryAcquire("DISCORD_API")).thenReturn(false);
        // when
        interceptor.intercept(context);
        // then
        verify(context).abortWith(argThat(response -> response.getStatus()
            .equals(HttpStatus.TOO_MANY_REQUESTS_429) && response.getBody() == null
        ));
    }
}
