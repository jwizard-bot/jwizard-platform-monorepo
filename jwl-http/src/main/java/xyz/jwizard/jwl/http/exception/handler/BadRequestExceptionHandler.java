package xyz.jwizard.jwl.http.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.json.JsonException;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;
import xyz.jwizard.jwl.http.validation.ValidationException;

public class BadRequestExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BadRequestExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return throwable instanceof IllegalArgumentException ||
            throwable instanceof JsonException ||
            throwable instanceof ValidationException;
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res, Throwable throwable) {
        LOG.warn("Bad request [{}]: {}", req.getPath(), throwable.getMessage());
        res.setStatus(HttpStatus.BAD_REQUEST_400);
        res.end();
    }
}
