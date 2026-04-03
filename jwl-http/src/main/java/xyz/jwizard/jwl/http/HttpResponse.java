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
package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.header.HttpHeaderName;
import xyz.jwizard.jwl.http.header.HttpHeaderValue;

public interface HttpResponse {
    String getHeaderUnsafe(String name);

    String getHeader(HttpHeaderName name);

    void setStatus(HttpStatus statusCode);

    void setHeader(HttpHeaderName name, HttpHeaderValue value);

    void setHeader(HttpHeaderName name, String value);

    void setHeaderUnsafe(String name, String value);

    void write(String body, boolean last);

    void writeEmpty(boolean last);

    void end();
}
