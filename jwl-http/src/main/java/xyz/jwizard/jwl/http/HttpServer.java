package xyz.jwizard.jwl.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.CollectionUtil;
import xyz.jwizard.jwl.http.exception.handler.AnnotatedExceptionHandler;
import xyz.jwizard.jwl.http.exception.handler.BadRequestExceptionHandler;
import xyz.jwizard.jwl.http.exception.handler.ExceptionHandler;
import xyz.jwizard.jwl.http.exception.handler.GlobalExceptionHandler;
import xyz.jwizard.jwl.http.filter.HttpFilter;
import xyz.jwizard.jwl.http.resolver.ArgumentResolver;
import xyz.jwizard.jwl.http.resolver.PathVariableResolver;
import xyz.jwizard.jwl.http.resolver.RequestBodyResolver;
import xyz.jwizard.jwl.http.resolver.RequestParamResolver;
import xyz.jwizard.jwl.http.route.Router;
import xyz.jwizard.jwl.http.route.TrieRouter;
import xyz.jwizard.jwl.http.validation.ValidationHandler;
import xyz.jwizard.jwl.http.validation.validator.LengthValidator;
import xyz.jwizard.jwl.http.validation.validator.NotNullValidator;
import xyz.jwizard.jwl.http.validation.validator.RangeValidator;
import xyz.jwizard.jwl.http.writer.*;

import java.io.Closeable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class HttpServer implements Closeable {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final ComponentProvider provider;
    protected final Router router;
    protected final Set<String> ignoredPaths;
    protected final int port;

    protected final Set<ArgumentResolver> resolvers;
    protected final Set<ResponseWriter> writers;
    protected final Set<ExceptionHandler> exceptionHandlers;

    private final Set<String> defaultIgnoredPaths = Set.of(
        "/favicon.ico",
        "/robots.txt"
    );

    protected HttpServer(AbstractBuilder<?> builder) {
        this.provider = builder.provider;
        this.router = new TrieRouter();
        this.port = builder.port;
        this.ignoredPaths = combinePaths(builder.ignoredPaths);

        final ValidationHandler validationHandler = new ValidationHandler(CollectionUtil
            .linkedSetOf(
                new NotNullValidator(),
                new LengthValidator(),
                new RangeValidator()
            ));
        resolvers = CollectionUtil.linkedSetOf(
            new PathVariableResolver(router),
            new RequestParamResolver(),
            new RequestBodyResolver(builder.jsonSerializer, validationHandler)
        );
        this.writers = initWriters(builder.jsonSerializer);
        this.exceptionHandlers = CollectionUtil.linkedSetOf(
            new AnnotatedExceptionHandler(),
            new BadRequestExceptionHandler(),
            new GlobalExceptionHandler()
        );
    }

    protected HttpRequestHandler prepareRequestHandler() {
        LOG.info("Scanning routes and preparing HTTP request handler");
        final RouteScanner scanner = new RouteScanner(provider, router, resolvers);
        scanner.scan();

        final List<HttpFilter> filters = provider.getInstancesOf(HttpFilter.class).stream()
            .sorted(Comparator.comparingInt(HttpFilter::order))
            .toList();

        return new HttpRequestHandler(router, ignoredPaths, filters, resolvers, writers,
            exceptionHandlers);
    }

    private Set<String> combinePaths(Set<String> customPaths) {
        final Set<String> combined = new HashSet<>(defaultIgnoredPaths);
        combined.addAll(customPaths);
        return Set.copyOf(combined);
    }

    private Set<ResponseWriter> initWriters(JsonSerializer json) {
        final Set<ResponseWriter> delegates = CollectionUtil.linkedSetOf(
            new VoidResponseWriter(),
            new StringResponseWriter(),
            new JsonResponseWriter(json)
        );
        final Set<ResponseWriter> all = CollectionUtil.linkedSetOf(
            new ResponseEntityResponseWriter(delegates)
        );
        all.addAll(delegates);
        return all;
    }

    public abstract void start();

    public abstract int getLocalPort();

    @Override
    public abstract void close();

    public abstract static class AbstractBuilder<B extends AbstractBuilder<B>> {
        protected final Set<String> ignoredPaths = new HashSet<>();

        protected ComponentProvider provider;
        protected JsonSerializer jsonSerializer;
        protected int port = 8080;

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B componentProvider(ComponentProvider provider) {
            this.provider = provider;
            return self();
        }

        public B jsonSerializer(JsonSerializer json) {
            this.jsonSerializer = json;
            return self();
        }

        public B port(int port) {
            this.port = port;
            return self();
        }

        public B ignoredPaths(Set<String> paths) {
            this.ignoredPaths.addAll(paths);
            return self();
        }

        protected void validate() {
            Assert.notNull(provider, "ComponentProvider is missing");
            Assert.notNull(jsonSerializer, "JsonSerializer is missing");
            Assert.state(port >= 0 && port < 65536, "Invalid port number");
        }

        public abstract HttpServer build();
    }
}
