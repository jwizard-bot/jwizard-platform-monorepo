package xyz.jwizard.jwl.http.exception.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.http.annotation.ResponseStatus;

public class AnnotatedExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return throwable.getClass().isAnnotationPresent(ResponseStatus.class);
    }

    @Override
    public void handle(Request req, Response res, Throwable throwable, Callback callback) {
        final int status = throwable.getClass().getAnnotation(ResponseStatus.class).value();
        LOG.warn("Annotated exception [{}]: {} -> Status {}",
            req.getHttpURI().getPath(), throwable.getClass().getSimpleName(), status);
        res.setStatus(status);
        callback.succeeded();
    }
}
