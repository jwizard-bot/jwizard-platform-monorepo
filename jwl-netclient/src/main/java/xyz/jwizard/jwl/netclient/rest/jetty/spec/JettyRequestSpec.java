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
package xyz.jwizard.jwl.netclient.rest.jetty.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.codec.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.net.http.HttpMethod;
import xyz.jwizard.jwl.netclient.rest.RestRequestException;
import xyz.jwizard.jwl.netclient.rest.RestResponse;
import xyz.jwizard.jwl.netclient.rest.pool.BaseUrlRegistryPool;
import xyz.jwizard.jwl.netclient.rest.spec.GenericRequestSpec;

public class JettyRequestSpec extends GenericRequestSpec {
    private static final Logger LOG = LoggerFactory.getLogger(JettyRequestSpec.class);

    private final HttpClient client;
    private final List<JettyBodyStrategy> bodyStrategies;

    public JettyRequestSpec(HttpClient client, BaseUrlRegistryPool baseUrlRegistryPool,
                            String uriPath, HttpMethod method,
                            SerializerRegistry<MessageSerializer> serializerRegistry,
                            ClassScanner scanner) {
        super(baseUrlRegistryPool, uriPath, method, serializerRegistry);
        this.client = client;
        bodyStrategies = loadRequestBodyStrategies(scanner);
    }

    @Override
    public <T> RestResponse<T> onSend(Class<T> responseType) {
        Request request = null;
        final String fullUri = resolveFullUri();
        try {
            LOG.debug("Sending {} request to: {}", method.name(), fullUri);
            request = client.newRequest(fullUri).method(method.name());
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                request.headers(h -> h.add(entry.getKey(), entry.getValue()));
            }
            for (final Map.Entry<String, String> entry : queryParams.entrySet()) {
                request.param(entry.getKey(), entry.getValue());
            }
            if (requestTimeout != null) {
                request.timeout(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            boolean strategyApplied = false;
            for (final JettyBodyStrategy strategy : bodyStrategies) {
                if (strategy.supports(this)) {
                    LOG.trace("Using body strategy: {}", strategy.getClass().getSimpleName());
                    request.body(strategy.buildContent(this, messageSerializer,
                        new JettyHeaderConsumer(request)));
                    strategyApplied = true;
                    break;
                }
            }
            if (!strategyApplied && body != null) {
                LOG.warn("Body is present but no suitable JettyBodyStrategy was found for " +
                    "request to: {}", fullUri);
            }
            final ContentResponse response = request.send();
            LOG.debug("Received response from {}: status={}", fullUri, response.getStatus());
            return new RestResponse<>(
                response.getStatus(),
                extractHeaders(response),
                parseResponseBody(response.getContent(), responseType)
            );
        } catch (Exception ex) {
            LOG.error("HTTP request failed, method: {}, uri: {}, error: {}", method.name(), fullUri,
                ex.getMessage());
            final String failUri = request != null ? request.getURI().toString() : uriPath;
            throw new RestRequestException(String.format("HTTP request failed: %s %s",
                method.name(), failUri), ex);
        }
    }

    private List<JettyBodyStrategy> loadRequestBodyStrategies(ClassScanner scanner) {
        LOG.info("Loading JettyBodyStrategies using scanner: {}",
            scanner.getClass().getSimpleName());
        final List<JettyBodyStrategy> strategies = new ArrayList<>();
        final Set<Class<? extends JettyBodyStrategy>> strategyClasses = scanner
            .getInstantiableSubtypesOf(JettyBodyStrategy.class);
        try {
            for (final Class<? extends JettyBodyStrategy> clazz : strategyClasses) {
                strategies.add(clazz.getDeclaredConstructor().newInstance());
                LOG.debug("Discovered and initialized strategy: {}", clazz.getName());
            }
        } catch (Exception ex) {
            throw new CriticalBootstrapException("Failed to auto-discover JettyBodyStrategy", ex);
        }
        LOG.info("Loaded {} request body strategy(ies)", strategies.size());
        return strategies;
    }

    private Map<String, List<String>> extractHeaders(Response jettyResponse) {
        final Map<String, List<String>> map = new HashMap<>();
        for (final HttpField field : jettyResponse.getHeaders()) {
            map.computeIfAbsent(field.getName(), k -> new ArrayList<>())
                .add(field.getValue());
        }
        return map;
    }
}
