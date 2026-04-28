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
package xyz.jwizard.jwl.netclient.rest.spec;

import java.time.Duration;

import xyz.jwizard.jwl.codec.serialization.SerializerFormat;
import xyz.jwizard.jwl.net.http.auth.AuthScheme;
import xyz.jwizard.jwl.net.http.auth.StandardAuthScheme;
import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;
import xyz.jwizard.jwl.net.http.header.HttpHeaderName;
import xyz.jwizard.jwl.net.http.header.HttpHeaderValue;
import xyz.jwizard.jwl.netclient.rest.RestResponse;
import xyz.jwizard.jwl.netclient.rest.intercept.RequestInterceptor;
import xyz.jwizard.jwl.netclient.rest.pool.UrlPool;

public interface RequestSpec {
    RequestSpec pool(UrlPool urlPool);

    default RequestSpec header(HttpHeaderName name, HttpHeaderValue value, Object... args) {
        return unsafeHeader(name, value.buildWithArgs(args));
    }

    RequestSpec unsafeHeader(HttpHeaderName name, String value);

    default RequestSpec auth(AuthScheme scheme, String... credentials) {
        return unsafeHeader(CommonHttpHeaderName.AUTHORIZATION,
            scheme.buildHeaderValue(credentials));
    }

    default RequestSpec bearerAuth(String token) {
        return auth(StandardAuthScheme.BEARER, token);
    }

    default RequestSpec basicAuth(String username, String password) {
        return auth(StandardAuthScheme.BASIC, username, password);
    }

    RequestSpec queryParam(String name, String value);

    RequestSpec formParam(String name, String value);

    RequestSpec body(Object body);

    RequestSpec serializer(SerializerFormat format);

    RequestSpec timeout(Duration timeout);

    // maxAttempts = maxRetries + 1
    RequestSpec retry(int maxRetries, Duration backoffMs);

    RequestSpec retry(int maxRetries, Duration backoffMs, Duration maxBackoffMs);

    RequestSpec disableRetry();

    RequestSpec interceptor(RequestInterceptor interceptor);

    <T> RestResponse<T> send(Class<T> responseType);

    default RestResponse<Void> send() {
        return send(Void.class);
    }
}
