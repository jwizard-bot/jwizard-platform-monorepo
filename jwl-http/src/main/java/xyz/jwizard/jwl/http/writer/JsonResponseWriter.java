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
package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.codec.serialization.json.JsonSerializer;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.header.CommonHttpHeaderName;
import xyz.jwizard.jwl.http.header.CommonHttpHeaderValue;

public class JsonResponseWriter implements ResponseWriter {
    private final JsonSerializer jsonSerializer;

    public JsonResponseWriter(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public boolean supports(Object result) {
        return result != null; // supports all which is not string and null
    }

    @Override
    public void write(HttpResponse res, Object result) {
        res.setHeader(CommonHttpHeaderName.CONTENT_TYPE,
            CommonHttpHeaderValue.APPLICATION_JSON_UTF_8);
        final String json = jsonSerializer.serialize(result);
        res.write(json, true);
    }
}
