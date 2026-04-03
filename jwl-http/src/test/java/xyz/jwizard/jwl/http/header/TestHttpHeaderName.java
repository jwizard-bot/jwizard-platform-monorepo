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
package xyz.jwizard.jwl.http.header;

public enum TestHttpHeaderName implements HttpHeaderName {
    AUTHORIZATION("Authorization"),
    X_SECURED_BY("X-Secured-By"),
    X_TEST_FILTER("X-Test-Filter"),
    X_FILTER_ORDER("X-Filter-Order"),
    ;

    private final String key;

    TestHttpHeaderName(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
