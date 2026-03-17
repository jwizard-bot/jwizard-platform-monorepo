package xyz.jwizard.jwl.transport.http.writer;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class VoidResponseWriter implements ResponseWriter {
    @Override
    public boolean supports(Object result) {
        return result == null;
    }

    @Override
    public void write(Response res, Object result, Callback callback) {
        res.setStatus(HttpStatus.NO_CONTENT_204);
        Content.Sink.write(res, true, "", callback);
    }
}
