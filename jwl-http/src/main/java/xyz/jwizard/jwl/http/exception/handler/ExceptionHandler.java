package xyz.jwizard.jwl.http.exception.handler;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;

public interface ExceptionHandler {
    boolean supports(Throwable throwable);

    void handle(HttpRequest req, HttpResponse res, Throwable throwable);
}
