package xyz.jwizard.jwl.http.writer;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class StringResponseWriter implements ResponseWriter {
    @Override
    public boolean supports(Object result) {
        return result instanceof String;
    }

    @Override
    public void write(Response res, Object result, Callback callback) {
        res.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain; charset=utf-8");
        Content.Sink.write(res, true, (String) result, callback);
    }
}
