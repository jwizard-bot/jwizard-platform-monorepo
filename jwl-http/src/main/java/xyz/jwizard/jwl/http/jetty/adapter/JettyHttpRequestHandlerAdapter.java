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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpRequestHandler;
import xyz.jwizard.jwl.http.HttpResponse;

public class JettyHttpRequestHandlerAdapter extends Handler.Abstract {
    private final HttpRequestHandler httpRequestHandler;

    public JettyHttpRequestHandlerAdapter(HttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        final HttpRequest req = new JettyHttpRequestAdapter(request);
        final HttpResponse res = new JettyHttpResponseAdapter(response, callback);
        try {
            httpRequestHandler.processRequest(req, res);
        } catch (Exception ex) {
            httpRequestHandler.handleException(req, res, ex);
        }
        return true;
    }
}
