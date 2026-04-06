/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.IdempotentService;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.serialization.json.JsonSerializer;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.CastUtil;
import xyz.jwizard.jwl.common.util.CollectionUtil;
import xyz.jwizard.jwl.http.annotation.Validator;
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
import xyz.jwizard.jwl.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.http.validation.ValidationHandler;
import xyz.jwizard.jwl.http.writer.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class HttpServer extends IdempotentService {
    protected final ComponentProvider componentProvider;
    protected final Router router;
    protected final Set<String> ignoredPaths;
    protected final int port;

    protected final List<HttpFilter> filters; // must be list or sorted set!
    protected final Set<ArgumentResolver> resolvers;
    protected final Set<ResponseWriter> writers;
    protected final Set<ExceptionHandler> exceptionHandlers;

    private final Set<String> defaultIgnoredPaths = Set.of(
        "/favicon.ico",
        "/robots.txt"
    );

    protected HttpServer(AbstractBuilder<?> builder) {
        componentProvider = builder.componentProvider;
        router = new TrieRouter();
        port = builder.port;
        ignoredPaths = combinePaths(builder.ignoredPaths);
        filters = componentProvider.getInstancesOf(HttpFilter.class)
            .stream()
            .sorted(Comparator.comparingInt(HttpFilter::order))
            .toList();
        final Set<AnnotationValidator<?>> validators = componentProvider
            .getInstancesAnnotatedWith(Validator.class)
            .stream()
            .map(obj -> (AnnotationValidator<?>) obj)
            .collect(Collectors.toSet());
        resolvers = CollectionUtil.linkedSetOf(
            new PathVariableResolver(router),
            new RequestParamResolver(),
            new RequestBodyResolver(builder.jsonSerializer, new ValidationHandler(validators))
        );
        writers = initWriters(builder.jsonSerializer);
        exceptionHandlers = CollectionUtil.linkedSetOf(
            new AnnotatedExceptionHandler(),
            new BadRequestExceptionHandler(),
            new GlobalExceptionHandler()
        );
        LOG.info("HTTP server initialized with:");
        LOG.info("-- {} filter(s) (via reflect api)", filters.size());
        LOG.info("-- {} validator(s) (via reflect api)", validators.size());
        LOG.info("-- {} resolver(s) (statically typed)", resolvers.size());
        LOG.info("-- {} writer(s) (statically typed)", writers.size());
        LOG.info("-- {} exception handler(s) (statically typed)", exceptionHandlers.size());
    }

    protected HttpRequestHandler prepareRequestHandler() {
        LOG.info("Scanning routes and preparing HTTP request handler");
        final RouteScanner scanner = new RouteScanner(componentProvider, router, resolvers);
        scanner.scan();
        return new HttpRequestHandler(
            router,
            ignoredPaths,
            filters,
            resolvers,
            writers,
            exceptionHandlers
        );
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

    public abstract int getLocalPort();

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>> {
        protected final Set<String> ignoredPaths = new HashSet<>();

        protected ComponentProvider componentProvider;
        protected JsonSerializer jsonSerializer;
        protected int port = 8080;

        protected AbstractBuilder() {
        }

        protected B self() {
            return CastUtil.unsafeCast(this);
        }

        public B componentProvider(ComponentProvider componentProvider) {
            this.componentProvider = componentProvider;
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
            Assert.notNull(componentProvider, "ComponentProvider cannot be null");
            Assert.notNull(jsonSerializer, "JsonSerializer cannot be null");
            Assert.state(port >= 0 && port < 65536, "Invalid port number");
        }

        public abstract HttpServer build();
    }
}
