package xyz.jwizard.jwl.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.http.annotation.Body;
import xyz.jwizard.jwl.http.annotation.HttpController;
import xyz.jwizard.jwl.http.annotation.HttpMethod;
import xyz.jwizard.jwl.http.annotation.RequestMapping;
import xyz.jwizard.jwl.http.route.Route;
import xyz.jwizard.jwl.http.route.Router;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

class RouteScanner {
    private static final Logger LOG = LoggerFactory.getLogger(RouteScanner.class);

    private final ClassScanner classScanner;
    private final Router router;
    private int scannedControllersCount = 0;
    private int registeredRoutesCount = 0;

    RouteScanner(ClassScanner classScanner, Router router) {
        this.classScanner = classScanner;
        this.router = router;
    }

    void scan() {
        final Set<Class<?>> controllers = classScanner.getTypesAnnotatedWith(HttpController.class);
        for (final Class<?> clazz : controllers) {
            processController(clazz);
        }
        LOG.info("Controllers initialized: {}", scannedControllersCount);
        LOG.info("Routes registered: {}", registeredRoutesCount);
    }

    private void processController(Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final Object instance = constructor.newInstance();
            final String basePath = clazz.getAnnotation(HttpController.class).value();
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    validateMethod(method);
                    registerMethodRoutes(instance, basePath, method);
                }
            }
            scannedControllersCount++;
        } catch (Exception ex) {
            LOG.error("Failed to initialize controller: {}", clazz.getName(), ex);
        }
    }

    private void validateMethod(Method method) {
        final long bodyParamsCount = Arrays.stream(method.getParameters())
            .filter(p -> p.isAnnotationPresent(Body.class))
            .count();
        if (bodyParamsCount > 1) {
            throw new IllegalStateException(String.format(
                "Invalid mapping in %s.%s(): multiple @Body detected. Only one is allowed.",
                method.getDeclaringClass().getSimpleName(), method.getName()
            ));
        }
    }

    private void registerMethodRoutes(Object instance, String basePath, Method method) {
        final RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        final String[] paths = mapping.value();
        final HttpMethod httpMethod = mapping.method();
        for (final String path : paths) {
            final String fullPath = combinePaths(basePath, path);
            router.addRoute(httpMethod.name(), fullPath, new Route(instance, method));
            registeredRoutesCount++;
            LOG.debug("Registered route: [{} {}] -> {}.{}()", httpMethod, fullPath,
                instance.getClass().getSimpleName(), method.getName());
        }
    }

    private String combinePaths(String prefix, String path) {
        if (prefix == null || prefix.isBlank() || prefix.equals("/")) {
            return ensureLeadingSlash(path);
        }
        final String cleanPrefix = prefix.replaceAll("/+$", ""); // remove slash from the end
        final String cleanPath = path.replaceAll("^/+", ""); // remove slash from the beginning
        return ensureLeadingSlash(cleanPrefix + "/" + cleanPath);
    }

    private String ensureLeadingSlash(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
