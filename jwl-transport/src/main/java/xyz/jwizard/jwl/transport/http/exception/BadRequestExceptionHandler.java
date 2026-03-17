package xyz.jwizard.jwl.transport.http.exception;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.json.JsonException;
import xyz.jwizard.jwl.transport.http.validation.ValidationException;

public class BadRequestExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BadRequestExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return throwable instanceof IllegalArgumentException ||
            throwable instanceof JsonException ||
            throwable instanceof ValidationException;
    }

    @Override
    public void handle(Request req, Response res, Throwable throwable, Callback callback) {
        LOG.warn("Bad request [{}]: {}", req.getHttpURI().getPath(), throwable.getMessage());
        res.setStatus(HttpStatus.BAD_REQUEST_400);
        callback.succeeded();
    }
}
