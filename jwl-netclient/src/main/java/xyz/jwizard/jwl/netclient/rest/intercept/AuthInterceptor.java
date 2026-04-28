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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.net.http.auth.AuthScheme;
import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;

public class AuthInterceptor implements RequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(AuthInterceptor.class);

    private final int order;
    private final AuthScheme scheme;
    private final String[] credentials;

    public AuthInterceptor(int order, AuthScheme scheme, String[] credentials) {
        this.order = order;
        this.scheme = scheme;
        this.credentials = credentials;
    }

    @Override
    public void intercept(InterceptorContext context) {
        final String authHeaderName = CommonHttpHeaderName.AUTHORIZATION.getCode();
        final boolean hasCustomAuth = context.getView().getHeaders()
            .containsKey(authHeaderName);
        if (!hasCustomAuth) {
            LOG.trace("Applying pool auth scheme: {}", scheme);
            context.setAuth(scheme, credentials);
        } else {
            LOG.trace("Skipping pool auth, request already has custom authorization.");
        }
    }

    @Override
    public int order() {
        return order;
    }
}
