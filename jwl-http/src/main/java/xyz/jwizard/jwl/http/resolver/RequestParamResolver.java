package xyz.jwizard.jwl.http.resolver;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import xyz.jwizard.jwl.http.annotation.RequestParam;
import xyz.jwizard.jwl.http.route.MatchResult;

import java.lang.reflect.Parameter;

public class RequestParamResolver implements ArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, Request req, MatchResult match) {
        final RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        final String paramName = annotation.value();

        final String query = req.getHttpURI().getQuery();
        final MultiMap<String> params = (query != null && !query.isEmpty())
            ? UrlEncoded.decodeQuery(query)
            : new MultiMap<>();

        String paramValue = params.getValue(paramName);
        if (paramValue == null) {
            if (annotation.defaultValue().isEmpty()) {
                if (annotation.required()) {
                    throw new IllegalArgumentException("Required request parameter '" + paramName
                        + "' is missing");
                }
                return null;
            }
            paramValue = annotation.defaultValue();
        }
        return ParameterConverter.parse(parameter.getType(), paramValue);
    }
}
