package xyz.jwizard.jwl.http.resolver;

import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.annotation.Body;
import xyz.jwizard.jwl.http.exception.RouteValidationException;
import xyz.jwizard.jwl.http.route.MatchResult;
import xyz.jwizard.jwl.http.validation.ValidationHandler;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

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
    public Object resolve(Parameter parameter, HttpRequest req, MatchResult match) throws Exception {
        if (req.getLength() == 0) {
            return null;
        }
        try (final InputStream in = req.getInputStream()) {
            final Object body = jsonSerializer.deserialize(in, parameter.getType());
            if (body != null) {
                validationHandler.validate(body);
            }
            return body;
        }
    }

    @Override
    public void validate(Method method) throws RouteValidationException {
        final long bodyParams = Arrays.stream(method.getParameters())
            .filter(this::supports)
            .count();
        if (bodyParams > 1) {
            throw new RouteValidationException(method,
                "Multiple @Body parameters detected, only one request body is allowed per route");
        }
    }
}
