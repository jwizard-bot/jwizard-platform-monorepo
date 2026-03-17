package xyz.jwizard.jwl.transport.http.resolver;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;
import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.transport.http.annotation.Body;
import xyz.jwizard.jwl.transport.http.route.MatchResult;
import xyz.jwizard.jwl.transport.http.validation.ValidationHandler;

import java.io.InputStream;
import java.lang.reflect.Parameter;

public class RequestBodyResolver implements ArgumentResolver {
    private final JsonSerializer jsonSerializer;
    private final ValidationHandler validationHandler;

    public RequestBodyResolver(JsonSerializer jsonSerializer, ValidationHandler validationHandler) {
        this.jsonSerializer = jsonSerializer;
        this.validationHandler = validationHandler;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Body.class);
    }

    @Override
    public Object resolve(Parameter parameter, Request req, MatchResult match) throws Exception {
        if (req.getLength() == 0) {
            return null;
        }
        try (final InputStream in = Content.Source.asInputStream(req)) {
            final Object body = jsonSerializer.deserialize(in, parameter.getType());
            if (body != null) {
                validationHandler.validate(body);
            }
            return body;
        }
    }
}
