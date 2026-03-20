package xyz.jwizard.jwl.http;

import java.io.InputStream;

public interface HttpRequest {
    String getMethod();

    String getPath();

    long getLength();

    InputStream getInputStream();

    String getQuery();

    String getQueryParam(String name);

    String getHeaderUnsafe(String name);
}
