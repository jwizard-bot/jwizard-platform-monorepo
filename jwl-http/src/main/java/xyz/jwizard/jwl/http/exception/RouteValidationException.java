package xyz.jwizard.jwl.http.exception;

import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;

import java.lang.reflect.Method;

public class RouteValidationException extends CriticalBootstrapException {
    public RouteValidationException(Method method, String details) {
        super(String.format("Invalid route configuration in %s.%s(): %s",
            method.getDeclaringClass().getSimpleName(), method.getName(), details));
    }
}
