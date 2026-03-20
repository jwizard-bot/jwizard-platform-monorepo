package xyz.jwizard.jwl.http.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;

public class GlobalExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return true;
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res, Throwable throwable) {
        LOG.error("Internal server error at {}: ", req.getPath(), throwable);
        res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
        res.end();
    }
}
