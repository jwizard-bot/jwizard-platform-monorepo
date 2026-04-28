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

import xyz.jwizard.jwl.net.http.auth.AuthScheme;
import xyz.jwizard.jwl.net.http.header.HttpHeaderName;
import xyz.jwizard.jwl.net.http.header.HttpHeaderValue;
import xyz.jwizard.jwl.netclient.rest.RestResponse;

public interface InterceptorContext {
    RequestView getView();

    default void addHeader(HttpHeaderName name, HttpHeaderValue value, Object... args) {
        addUnsafeHeader(name, value.buildWithArgs(args));
    }

    void addUnsafeHeader(HttpHeaderName name, String value);

    void addQueryParam(String name, String value);

    void setAuth(AuthScheme scheme, String... credentials);

    void abortWith(RestResponse<?> response);
}
