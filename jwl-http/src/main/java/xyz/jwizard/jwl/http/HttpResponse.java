package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.header.HttpHeaderName;
import xyz.jwizard.jwl.http.header.HttpHeaderValue;

public interface HttpResponse {
    String getHeaderUnsafe(String name);

    String getHeader(HttpHeaderName name);

    void setStatus(HttpStatus statusCode);

    void setHeader(HttpHeaderName name, HttpHeaderValue value);

    void setHeader(HttpHeaderName name, String value);

    void setHeaderUnsafe(String name, String value);

    void write(String body, boolean last);

    void writeEmpty(boolean last);

    void end();
}
