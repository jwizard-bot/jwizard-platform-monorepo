package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.common.serialization.json.JsonSerializer;
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
