package xyz.jwizard.jwl.http;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.cache.ProviderCache;
import xyz.jwizard.jwl.http.exception.handler.ExceptionHandler;
import xyz.jwizard.jwl.http.resolver.ArgumentResolver;
import xyz.jwizard.jwl.http.route.MatchResult;
import xyz.jwizard.jwl.http.route.Router;
import xyz.jwizard.jwl.http.writer.ResponseWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

class RequestHandler extends Handler.Abstract {
    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Router router;
    private final Set<String> ignoredPaths; // must be set! (O(1), table/list are much slower)

    // cache for O(1) complexity, lists are BAD :(
    private final ProviderCache<Parameter, Parameter, ArgumentResolver> resolverCache;
    private final ProviderCache<Class<?>, Object, ResponseWriter> writerCache;
    private final ProviderCache<Class<? extends Throwable>, Throwable, ExceptionHandler>
        exceptionCache;

    RequestHandler(Router router,
                   Set<String> ignoredPaths,
                   Set<ArgumentResolver> resolvers,
                   Set<ResponseWriter> writers,
                   Set<ExceptionHandler> exceptionHandlers) {
        this.router = router;
        this.ignoredPaths = ignoredPaths;
        resolverCache = new ProviderCache<>(resolvers, ArgumentResolver::supports);
        writerCache = new ProviderCache<>(writers, ResponseWriter::supports);
        exceptionCache = new ProviderCache<>(exceptionHandlers, ExceptionHandler::supports);
        LOG.info("RequestHandler initialized with {} resolvers, {} writers, {} exception handlers",
            resolvers.size(), writers.size(), exceptionHandlers.size());
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        final String method = request.getMethod();
        final String path = request.getHttpURI().getPath();

        LOG.debug("Incoming request: {} {}", method, path);

        if (ignoredPaths.contains(path)) {
            LOG.debug("Path {} is in ignored paths list, skipping", path);
            return finish(response, HttpStatus.NO_CONTENT_204, callback);
        }
        final MatchResult match = router.findRoute(method, path);
        if (match == null) {
            LOG.debug("No route found for {} {}", method, path);
            return finish(response, HttpStatus.NOT_FOUND_404, callback);
        }
        LOG.debug("Route matched: {} -> {}.{}()", path,
            match.route().instance().getClass().getSimpleName(), match.route().method().getName());
        try {
            final Object result = execute(request, match);
            return processResponse(response, result, callback);
        } catch (Exception e) {
            return handleException(request, response, e, callback);
        }
    }

    private Object execute(Request req, MatchResult match) throws Exception {
        final Method actionMethod = match.route().method();
        actionMethod.setAccessible(true);

        final Parameter[] parameters = actionMethod.getParameters();
        final Object[] args = new Object[parameters.length];
        if (parameters.length > 0) {
            LOG.debug("Resolving {} parameters for method {}", parameters.length,
                actionMethod.getName());
        }
        for (int i = 0; i < parameters.length; i++) {
            final Parameter param = parameters[i];
            final ArgumentResolver resolver = resolverCache.get(param, param);
            if (resolver != null) {
                LOG.debug("Parameter '{}' [{}] resolved by {}", param.getName(),
                    param.getType().getSimpleName(), resolver.getClass().getSimpleName());
                args[i] = resolver.resolve(param, req, match);
            } else {
                LOG.warn("No resolver found for parameter '{}' [{}]", param.getName(),
                    param.getType().getSimpleName());
            }
        }
        return actionMethod.invoke(match.route().instance(), args);
    }

    private boolean processResponse(Response res, Object result, Callback callback)
        throws Exception {
        if (!(result instanceof ResponseEntity<?>)) {
            res.setStatus(HttpStatus.OK_200);
        }
        final Class<?> resultClass = (result == null) ? void.class : result.getClass();
        if (LOG.isDebugEnabled() && result instanceof ResponseEntity<?> entity) {
            LOG.debug("Result is ResponseEntity with status {}", entity.status());
        }
        final ResponseWriter writer = writerCache.get(resultClass, result);
        if (writer != null) {
            LOG.debug("Writing response using {}", writer.getClass().getSimpleName());
            writer.write(res, result, callback);
            return true;
        }
        LOG.error("No suitable ResponseWriter found for result class: {}", resultClass);
        return finish(res, HttpStatus.INTERNAL_SERVER_ERROR_500, callback);
    }

    private boolean handleException(Request req, Response res, Exception ex, Callback callback) {
        final Throwable cause = (ex instanceof InvocationTargetException) ? ex.getCause() : ex;
        final Class<? extends Throwable> causeClass = cause.getClass();

        LOG.debug("Exception caught during request execution: {}", causeClass.getName());

        final ExceptionHandler handler = exceptionCache.get(causeClass, cause);
        if (handler != null) {
            LOG.debug("Exception handled by {}", handler.getClass().getSimpleName());
            handler.handle(req, res, cause, callback);
            return true;
        }
        return finish(res, HttpStatus.INTERNAL_SERVER_ERROR_500, callback);
    }

    private boolean finish(Response response, int status, Callback callback) {
        LOG.debug("Finishing request with status: {}", status);
        response.setStatus(status);
        callback.succeeded();
        return true;
    }
}
