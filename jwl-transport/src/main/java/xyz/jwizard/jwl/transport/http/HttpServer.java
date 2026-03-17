package xyz.jwizard.jwl.transport.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.CollectionUtil;
import xyz.jwizard.jwl.common.json.JacksonSerializer;
import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.transport.http.exception.AnnotatedExceptionHandler;
import xyz.jwizard.jwl.transport.http.exception.BadRequestExceptionHandler;
import xyz.jwizard.jwl.transport.http.exception.ExceptionHandler;
import xyz.jwizard.jwl.transport.http.exception.GlobalExceptionHandler;
import xyz.jwizard.jwl.transport.http.resolver.ArgumentResolver;
import xyz.jwizard.jwl.transport.http.resolver.PathVariableResolver;
import xyz.jwizard.jwl.transport.http.resolver.RequestBodyResolver;
import xyz.jwizard.jwl.transport.http.resolver.RequestParamResolver;
import xyz.jwizard.jwl.transport.http.route.Router;
import xyz.jwizard.jwl.transport.http.route.TrieRouter;
import xyz.jwizard.jwl.transport.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.transport.http.validation.ValidationHandler;
import xyz.jwizard.jwl.transport.http.validation.validator.LengthValidator;
import xyz.jwizard.jwl.transport.http.validation.validator.NotNullValidator;
import xyz.jwizard.jwl.transport.http.validation.validator.RangeValidator;
import xyz.jwizard.jwl.transport.http.writer.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

    private static final Set<String> DEFAULT_IGNORED_PATHS = Set.of(
        "/favicon.ico",
        "/robots.txt"
    );
    private final Set<String> ignoredPaths;
    private final String basePackageToScan;
    private final int port;
    private final boolean blockingMode;
    private final Set<ArgumentResolver> resolvers;
    private final Set<ResponseWriter> writers;
    private final Set<ExceptionHandler> exceptionHandlers;
    private Server server;
    private ServerConnector connector;

    private HttpServer(Builder builder) {
        ignoredPaths = builder.ignoredPaths;
        basePackageToScan = builder.basePackageToScan;
        port = builder.port;
        blockingMode = builder.blockingMode;

        // validators init
        final Set<AnnotationValidator<?>> allValidators = new LinkedHashSet<>();
        allValidators.add(new NotNullValidator());
        allValidators.add(new LengthValidator());
        allValidators.add(new RangeValidator());
        allValidators.addAll(builder.validators);
        final ValidationHandler validationHandler = new ValidationHandler(allValidators);

        // resolvers init
        resolvers = new LinkedHashSet<>(builder.resolvers);
        resolvers.add(new PathVariableResolver());
        resolvers.add(new RequestParamResolver());
        resolvers.add(new RequestBodyResolver(builder.jsonSerializer, validationHandler));

        // writers init
        final Set<ResponseWriter> delegates = CollectionUtil.linkedSetOf(
            new VoidResponseWriter(),
            new StringResponseWriter(),
            new JsonResponseWriter(builder.jsonSerializer)
        );
        delegates.addAll(builder.writers);
        writers = CollectionUtil.linkedSetOf(
            new ResponseEntityResponseWriter(delegates) // must be first!
        );
        writers.addAll(delegates);

        // exception handlers init
        exceptionHandlers = new LinkedHashSet<>(builder.exceptionHandlers);
        exceptionHandlers.add(new AnnotatedExceptionHandler());
        exceptionHandlers.add(new BadRequestExceptionHandler());
        exceptionHandlers.add(new GlobalExceptionHandler());
    }

    public static Builder builder() {
        return new Builder();
    }

    public void start() {
        LOG.info("Initializing HTTP server...");

        final Router router = new TrieRouter();
        final RouteScanner scanner = new RouteScanner(basePackageToScan, router);
        scanner.scan();

        final Set<String> combinedIgnoredPaths = combineIgnoredPaths();
        LOG.info("HTTP ignored paths: {}", combinedIgnoredPaths);

        final QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
        queuedThreadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
        queuedThreadPool.setName("jwl-vt-pool");

        server = new Server(queuedThreadPool);
        connector = new ServerConnector(server);
        connector.setPort(port);

        server.addConnector(connector);
        server.setHandler(new RequestHandler(
            router,
            combinedIgnoredPaths,
            resolvers,
            writers,
            exceptionHandlers
        ));
        server.setStopAtShutdown(true);
        try {
            server.start();
            LOG.info("HTTP server is listening on http://localhost:{}", port);
            if (blockingMode) {
                server.join();
            }
        } catch (Exception ex) {
            LOG.error("Failed to start HTTP server", ex);
            throw new RuntimeException("HTTP server startup failed", ex);
        }
    }

    // only for unit/integration tests
    void stop() {
        if (server == null || !server.isRunning()) {
            return;
        }
        try {
            LOG.info("Stopping HTTP server...");
            server.stop();
            server.join();
            LOG.info("HTTP server stopped successfully.");
        } catch (Exception ex) {
            LOG.error("Error while stopping HTTP server", ex);
        }
    }

    public int getLocalPort() {
        if (connector == null || !connector.isRunning()) {
            throw new IllegalStateException("Connector (from server) is not running!");
        }
        return connector.getLocalPort();
    }

    private Set<String> combineIgnoredPaths() {
        final Set<String> combinedPaths = new HashSet<>(DEFAULT_IGNORED_PATHS);
        if (!ignoredPaths.isEmpty()) {
            combinedPaths.addAll(ignoredPaths);
        }
        return Set.copyOf(combinedPaths); // direct copy
    }

    public static class Builder {
        private final Set<String> ignoredPaths = new HashSet<>();
        private final LinkedHashSet<ArgumentResolver> resolvers = new LinkedHashSet<>();
        private final LinkedHashSet<ResponseWriter> writers = new LinkedHashSet<>();
        private final LinkedHashSet<ExceptionHandler> exceptionHandlers = new LinkedHashSet<>();
        private final LinkedHashSet<AnnotationValidator<?>> validators = new LinkedHashSet<>();
        private JsonSerializer jsonSerializer = new JacksonSerializer();
        private String basePackageToScan = "xyz.jwizard";
        private int port = 8080;
        private boolean blockingMode = true;

        private Builder() {
        }

        public Builder ignoredPaths(Set<String> paths) {
            this.ignoredPaths.addAll(paths);
            return this;
        }

        public Builder appendResolvers(LinkedHashSet<ArgumentResolver> resolvers) {
            this.resolvers.addAll(resolvers);
            return this;
        }

        public Builder appendWriters(LinkedHashSet<ResponseWriter> writers) {
            this.writers.addAll(writers);
            return this;
        }

        public Builder appendExceptionHandlers(LinkedHashSet<ExceptionHandler> exceptionHandlers) {
            this.exceptionHandlers.addAll(exceptionHandlers);
            return this;
        }

        public Builder appendValidators(LinkedHashSet<AnnotationValidator<?>> validators) {
            this.validators.addAll(validators);
            return this;
        }

        public Builder basePackageToScan(String basePackageToScan) {
            this.basePackageToScan = basePackageToScan;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder blockingMode(boolean blockingMode) {
            this.blockingMode = blockingMode;
            return this;
        }

        public Builder objectMapper(JsonSerializer jsonSerializer) {
            this.jsonSerializer = jsonSerializer;
            return this;
        }

        public HttpServer build() {
            return new HttpServer(this);
        }
    }
}
