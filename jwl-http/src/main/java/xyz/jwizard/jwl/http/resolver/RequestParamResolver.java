package xyz.jwizard.jwl.http.resolver;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.annotation.RequestParam;
import xyz.jwizard.jwl.http.route.MatchResult;

import java.lang.reflect.Parameter;

public class RequestParamResolver implements ArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest req, MatchResult match) {
        final RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        final String paramName = annotation.value();

        String paramValue = req.getQueryParam(paramName);
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
