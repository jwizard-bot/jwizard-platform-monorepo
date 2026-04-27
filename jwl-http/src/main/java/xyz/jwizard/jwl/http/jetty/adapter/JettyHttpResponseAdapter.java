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
package xyz.jwizard.jwl.http.jetty.adapter;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.net.http.HttpStatus;
import xyz.jwizard.jwl.net.http.header.HttpHeaderName;
import xyz.jwizard.jwl.net.http.header.HttpHeaderValue;

public class JettyHttpResponseAdapter implements HttpResponse {
    private final Response response;
    private final Callback callback;

    public JettyHttpResponseAdapter(Response response, Callback callback) {
        this.response = response;
        this.callback = callback;
    }

    @Override
    public String getHeaderUnsafe(String name) {
        return response.getHeaders().get(name);
    }

    @Override
    public String getHeader(HttpHeaderName name) {
        return getHeaderUnsafe(name.getCode());
    }

    @Override
    public void setStatus(HttpStatus statusCode) {
        response.setStatus(statusCode.getCode());
    }

    @Override
    public void setHeader(HttpHeaderName name, HttpHeaderValue value, Object... args) {
        setHeaderUnsafe(name.getCode(), value.buildWithArgs(args));
    }

    @Override
    public void setHeader(HttpHeaderName name, String value) {
        setHeaderUnsafe(name.getCode(), value);
    }

    @Override
    public void setHeaderUnsafe(String name, String value) {
        // put means override, add create new header with same key
        response.getHeaders().put(name, value);
    }

    @Override
    public void write(String body, boolean last) {
        Content.Sink.write(response, last, body, callback);
    }

    @Override
    public void writeEmpty(boolean last) {
        write("", last);
    }

    @Override
    public void end() {
        callback.succeeded();
    }
}
