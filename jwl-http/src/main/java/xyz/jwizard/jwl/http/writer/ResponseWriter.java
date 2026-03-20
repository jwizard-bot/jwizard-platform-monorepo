package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.http.HttpResponse;

public interface ResponseWriter {
    boolean supports(Object result);

    void write(HttpResponse res, Object result) throws Exception;
}
