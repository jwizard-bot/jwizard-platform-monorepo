package xyz.jwizard.jwl.transport.http.resolver;

import org.eclipse.jetty.server.Request;
import xyz.jwizard.jwl.transport.http.annotation.PathVariable;
import xyz.jwizard.jwl.transport.http.route.MatchResult;

import java.lang.reflect.Parameter;

public class PathVariableResolver implements ArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, Request req, MatchResult match) {
        final PathVariable ann = parameter.getAnnotation(PathVariable.class);
        final String name = ann.value();
        final boolean isRequired = ann.required();

        final String value = match.variables().get(name);
        if (value == null && isRequired) {
            throw new IllegalArgumentException("Path variable '" + name +
                "' is required but not found in URL");
        }
        if (value == null) {
            return null;
        }
        return ParameterConverter.parse(parameter.getType(), value);
    }
}
