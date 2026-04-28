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
package xyz.jwizard.jwl.netclient.rest;

import xyz.jwizard.jwl.net.http.header.HttpHeaderName;

public enum TestHttpHeaderName implements HttpHeaderName {
    X_CORRELATION_ID("X-Correlation-Id"),
    X_ACTION_TYPE("X-Action-Type"),
    X_REQUEST_SIGNATURE("X-Request-Signature"),
    ;

    private final String code;

    TestHttpHeaderName(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
