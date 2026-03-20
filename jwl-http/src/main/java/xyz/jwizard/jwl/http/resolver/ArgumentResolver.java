package xyz.jwizard.jwl.http.resolver;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.exception.RouteValidationException;
import xyz.jwizard.jwl.http.route.MatchResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);

    // runs per request, avoid O(N) operations
    Object resolve(Parameter parameter, HttpRequest req, MatchResult match) throws Exception;

    // runs once, at application startup
    default void validate(Method method) throws RouteValidationException {
    }
}
