package xyz.jwizard.jwl.http.jetty.adapter;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import xyz.jwizard.jwl.http.HttpHeader;
import xyz.jwizard.jwl.http.HttpHeaderName;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;

public class JettyHttpResponseAdapter implements HttpResponse {
    private final Response response;
    private final Callback callback;

    public JettyHttpResponseAdapter(Response response, Callback callback) {
        this.response = response;
        this.callback = callback;
    }

    @Override
    public void setStatus(HttpStatus statusCode) {
        response.setStatus(statusCode.getCode());
    }

    @Override
    public void setHeader(HttpHeaderName name, HttpHeader value) {
        setHeaderUnsafe(name.getKey(), value.getKey());
    }

    @Override
    public void setHeaderUnsafe(String name, String value) {
        response.getHeaders().add(name, value);
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
