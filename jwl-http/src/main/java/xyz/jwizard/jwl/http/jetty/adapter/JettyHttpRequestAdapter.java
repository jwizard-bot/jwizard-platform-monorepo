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

import java.io.InputStream;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;
import xyz.jwizard.jwl.net.http.header.HttpHeaderName;

public class JettyHttpRequestAdapter implements HttpRequest {
    private final Request request;
    private MultiMap<String> queryParams;

    public JettyHttpRequestAdapter(Request request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPath() {
        return request.getHttpURI().getPath();
    }

    @Override
    public long getLength() {
        return request.getLength();
    }

    @Override
    public InputStream getInputStream() {
        return Content.Source.asInputStream(request);
    }

    @Override
    public String getQuery() {
        return request.getHttpURI().getQuery();
    }

    @Override
    public String getQueryParam(String name) {
        if (queryParams == null) {
            final String query = getQuery();
            if (query != null && !query.isEmpty()) {
                queryParams = UrlEncoded.decodeQuery(query);
            } else {
                queryParams = new MultiMap<>();
            }
        }
        return queryParams.getValue(name);
    }

    @Override
    public String getHeader(HttpHeaderName name) {
        return getHeaderUnsafe(name.getCode());
    }

    @Override
    public String getHeaderUnsafe(String name) {
        return request.getHeaders().get(name);
    }

    @Override
    public String getContentType() {
        final String contentType = getHeader(CommonHttpHeaderName.CONTENT_TYPE);
        if (contentType == null) {
            return null;
        }
        final int semicolonIndex = contentType.indexOf(';');
        final String rawType = (semicolonIndex != -1)
            ? contentType.substring(0, semicolonIndex)
            : contentType;
        return rawType.trim().toLowerCase();
    }
}
