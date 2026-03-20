package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.header.HttpHeaderName;

import java.io.InputStream;

public interface HttpRequest {
    String getMethod();

    String getPath();

    long getLength();

    InputStream getInputStream();

    String getQuery();

    String getQueryParam(String name);

    String getHeader(HttpHeaderName name);

    String getHeaderUnsafe(String name);
}
