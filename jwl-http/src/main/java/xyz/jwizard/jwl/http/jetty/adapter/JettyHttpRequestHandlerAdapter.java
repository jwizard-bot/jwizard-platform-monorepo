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
