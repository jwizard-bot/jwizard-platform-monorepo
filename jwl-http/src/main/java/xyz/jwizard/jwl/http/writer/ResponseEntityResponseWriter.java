package xyz.jwizard.jwl.http.writer;

import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.http.ResponseEntity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// implements cache, O(1) complexity
public class ResponseEntityResponseWriter implements ResponseWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseEntityResponseWriter.class);

    private final Set<ResponseWriter> delegates;
    private final Map<Class<?>, ResponseWriter> writerCache = new ConcurrentHashMap<>();

    public ResponseEntityResponseWriter(Set<ResponseWriter> delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean supports(Object result) {
        return result instanceof ResponseEntity;
    }

    @Override
    public void write(Response res, Object result, Callback callback) throws Exception {
        final ResponseEntity<?> entity = (ResponseEntity<?>) result;
        res.setStatus(entity.status());

        final Object body = entity.body();
        final Class<?> bodyClass = (body == null) ? void.class : body.getClass();

        final ResponseWriter writer = writerCache.computeIfAbsent(bodyClass, clazz ->
            delegates.stream()
                .filter(w -> w != this && w.supports(body))
                .findFirst()
                .orElse(null)
        );
        if (writer != null) {
            writer.write(res, body, callback);
            return;
        }
        LOG.error("No suitable ResponseWriter found for body type: {}", bodyClass);
        callback.succeeded();
    }
}
