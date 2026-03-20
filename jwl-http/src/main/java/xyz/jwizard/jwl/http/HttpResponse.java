package xyz.jwizard.jwl.http;

public interface HttpResponse {
    void setStatus(HttpStatus statusCode);

    void setHeader(HttpHeaderName name, HttpHeader value);

    void setHeaderUnsafe(String name, String value);

    void write(String body, boolean last);

    void writeEmpty(boolean last);

    void end();
}
