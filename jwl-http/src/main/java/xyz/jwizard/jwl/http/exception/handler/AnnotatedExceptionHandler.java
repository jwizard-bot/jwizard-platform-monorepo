package xyz.jwizard.jwl.http.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;
import xyz.jwizard.jwl.http.annotation.ResponseStatus;

public class AnnotatedExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return throwable.getClass().isAnnotationPresent(ResponseStatus.class);
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res, Throwable throwable) {
        final HttpStatus status = throwable.getClass().getAnnotation(ResponseStatus.class).value();
        LOG.warn("Annotated exception [{}]: {} -> Status {}",
            req.getPath(), throwable.getClass().getSimpleName(), status);
        res.setStatus(status);
        res.end();
    }
}
