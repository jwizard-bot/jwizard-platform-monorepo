package xyz.jwizard.jwl.transport.http.exception;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public interface ExceptionHandler {
    boolean supports(Throwable throwable);

    void handle(Request req, Response res, Throwable throwable, Callback callback);
}
