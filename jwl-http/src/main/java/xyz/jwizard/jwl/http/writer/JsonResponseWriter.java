package xyz.jwizard.jwl.http.writer;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import xyz.jwizard.jwl.common.json.JsonSerializer;

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
    public void write(Response res, Object result, Callback callback) {
        res.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=utf-8");
        final String json = jsonSerializer.serialize(result);
        Content.Sink.write(res, true, json, callback);
    }
}
