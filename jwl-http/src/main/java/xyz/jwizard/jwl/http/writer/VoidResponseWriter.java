package xyz.jwizard.jwl.http.writer;

import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;

public class VoidResponseWriter implements ResponseWriter {
    @Override
    public boolean supports(Object result) {
        return result == null;
    }

    @Override
    public void write(HttpResponse res, Object result) {
        res.setStatus(HttpStatus.NO_CONTENT_204);
        res.writeEmpty(true);
    }
}
