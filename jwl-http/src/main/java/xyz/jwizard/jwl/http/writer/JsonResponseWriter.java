package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.http.HttpHeader;
import xyz.jwizard.jwl.http.HttpHeaderName;
import xyz.jwizard.jwl.http.HttpResponse;

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
        res.setHeader(HttpHeaderName.CONTENT_TYPE, HttpHeader.APPLICATION_JSON_UTF_8);
        final String json = jsonSerializer.serialize(result);
        res.write(json, true);
    }
}
