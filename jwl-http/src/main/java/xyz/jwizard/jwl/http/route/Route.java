package xyz.jwizard.jwl.http.route;

import java.lang.reflect.Method;

public record Route(Object instance, Method method) {
}
