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

import java.util.List;
import java.util.Map;

import xyz.jwizard.jwl.net.http.HttpStatus;

public class RestResponse<T> {
    private final HttpStatus status;
    private final Map<String, List<String>> headers;
    private final T body;

    public RestResponse(int status, Map<String, List<String>> headers, T body) {
        this.status = HttpStatus.fromCode(status);
        this.headers = headers != null ? headers : Map.of();
        this.body = body;
    }

    public boolean is(HttpStatus status) {
        return this.status.equals(status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }
}
