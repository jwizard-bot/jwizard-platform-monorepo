package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.http.HttpHeader;
import xyz.jwizard.jwl.http.HttpHeaderName;
import xyz.jwizard.jwl.http.HttpResponse;

public class StringResponseWriter implements ResponseWriter {
    @Override
    public boolean supports(Object result) {
        return result instanceof String;
    }

    @Override
    public void write(HttpResponse res, Object result) {
        res.setHeader(HttpHeaderName.CONTENT_TYPE, HttpHeader.TEXT_PLAIN_UTF_8);
        res.write((String) result, true);
    }
}
