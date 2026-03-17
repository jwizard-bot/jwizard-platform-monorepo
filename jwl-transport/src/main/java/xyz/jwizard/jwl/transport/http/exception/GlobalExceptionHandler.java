package xyz.jwizard.jwl.transport.http.exception;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return true;
    }

    @Override
    public void handle(Request req, Response res, Throwable throwable, Callback callback) {
        LOG.error("Internal server error at {}: ", req.getHttpURI().getPath(), throwable);
        res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
        callback.succeeded();
    }
}
