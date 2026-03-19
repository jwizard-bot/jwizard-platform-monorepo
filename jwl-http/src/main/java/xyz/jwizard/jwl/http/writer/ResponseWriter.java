package xyz.jwizard.jwl.http.writer;

import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public interface ResponseWriter {
    boolean supports(Object result);

    void write(Response res, Object result, Callback callback) throws Exception;
}
