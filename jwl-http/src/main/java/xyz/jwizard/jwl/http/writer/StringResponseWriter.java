package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.header.CommonHttpHeaderName;
import xyz.jwizard.jwl.http.header.CommonHttpHeaderValue;

public class StringResponseWriter implements ResponseWriter {
    @Override
    public boolean supports(Object result) {
        return result instanceof String;
    }

    @Override
    public void write(HttpResponse res, Object result) {
        res.setHeader(CommonHttpHeaderName.CONTENT_TYPE, CommonHttpHeaderValue.TEXT_PLAIN_UTF_8);
        res.write((String) result, true);
    }
}
