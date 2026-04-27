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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.util.PathUtil;
import xyz.jwizard.jwl.http.annotation.HttpController;
import xyz.jwizard.jwl.http.annotation.RequestMapping;
import xyz.jwizard.jwl.http.resolver.ArgumentResolver;
import xyz.jwizard.jwl.http.route.Route;
import xyz.jwizard.jwl.http.route.Router;
import xyz.jwizard.jwl.net.http.HttpMethod;

class RouteScanner {
    private static final Logger LOG = LoggerFactory.getLogger(RouteScanner.class);

    private final ComponentProvider componentProvider;
    private final Router router;
    private final Set<ArgumentResolver> resolvers;

    private int registeredRoutesCount = 0;

    RouteScanner(ComponentProvider componentProvider, Router router,
                 Set<ArgumentResolver> resolvers) {
        this.componentProvider = componentProvider;
        this.router = router;
        this.resolvers = resolvers;
    }

    void scan() {
        final Collection<Object> instances = componentProvider
            .getInstancesAnnotatedWith(HttpController.class);
        for (final Object instance : instances) {
            registerRoutesForInstance(instance);
        }
        LOG.info("Initialized {} HTTP controller(s)", instances.size());
        LOG.info("Total routes registered: {}", registeredRoutesCount);
    }

    private void registerRoutesForInstance(Object instance) {
        final Class<?> clazz = instance.getClass();
        final String basePath = clazz.getAnnotation(HttpController.class).value();
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                registerMethodRoutes(instance, basePath, method);
                validateMethod(method);
            }
        }
    }

    private void validateMethod(Method method) {
        for (final ArgumentResolver resolver : resolvers) {
            resolver.validate(method);
        }
    }

    private void registerMethodRoutes(Object instance, String basePath, Method method) {
        final RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        final String[] paths = mapping.value();
        final HttpMethod httpMethod = mapping.method();
        for (final String path : paths) {
            final String fullPath = PathUtil.combinePaths(basePath, path);
            router.addRoute(httpMethod.name(), fullPath, new Route(instance, method, path));
            registeredRoutesCount++;
            LOG.debug("Registered route: [{} {}] -> {}.{}()", httpMethod, fullPath,
                instance.getClass().getSimpleName(), method.getName());
        }
    }
}
