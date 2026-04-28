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
package xyz.jwizard.jwl.netclient.rest.spec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.codec.serialization.SerializerFormat;
import xyz.jwizard.jwl.codec.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.Ordered;
import xyz.jwizard.jwl.common.retry.RetryExecutor;
import xyz.jwizard.jwl.common.util.CastUtil;
import xyz.jwizard.jwl.common.util.CollectionUtil;
import xyz.jwizard.jwl.net.NetworkUtil;
import xyz.jwizard.jwl.net.http.HttpMethod;
import xyz.jwizard.jwl.net.http.HttpStatus;
import xyz.jwizard.jwl.net.http.auth.AuthScheme;
import xyz.jwizard.jwl.net.http.header.HttpHeaderName;
import xyz.jwizard.jwl.netclient.rest.RestRequestException;
import xyz.jwizard.jwl.netclient.rest.RestResponse;
import xyz.jwizard.jwl.netclient.rest.intercept.InterceptorContext;
import xyz.jwizard.jwl.netclient.rest.intercept.RequestInterceptor;
import xyz.jwizard.jwl.netclient.rest.intercept.RequestView;
import xyz.jwizard.jwl.netclient.rest.pool.BaseUrlRegistryPool;
import xyz.jwizard.jwl.netclient.rest.pool.DefaultUrlPool;
import xyz.jwizard.jwl.netclient.rest.pool.UrlPool;
import xyz.jwizard.jwl.netclient.rest.retry.RetryPolicy;

public abstract class GenericRequestSpec implements RequestSpec, RequestView {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseUrlRegistryPool baseUrlRegistryPool;
    protected final String uriPath;
    protected final HttpMethod method;
    protected final SerializerRegistry<MessageSerializer> serializerRegistry;

    protected final List<RequestInterceptor> interceptors = new ArrayList<>();
    protected final Map<String, String> headers = new LinkedHashMap<>();
    protected final Map<String, String> queryParams = new LinkedHashMap<>();
    protected final Map<String, String> formParams = new LinkedHashMap<>();

    protected UrlPool urlPool;
    protected MessageSerializer messageSerializer;
    protected Object body;
    protected Duration requestTimeout;
    protected RestResponse<?> abortedResponse;

    protected final InterceptorContext reusableContext = new InterceptorContext() {
        @Override
        public RequestView getView() {
            return GenericRequestSpec.this;
        }

        @Override
        public void addUnsafeHeader(HttpHeaderName name, String value) {
            GenericRequestSpec.this.unsafeHeader(name, value);
        }

        @Override
        public void addQueryParam(String name, String value) {
            GenericRequestSpec.this.queryParam(name, value);
        }

        @Override
        public void setAuth(AuthScheme scheme, String... credentials) {
            GenericRequestSpec.this.auth(scheme, credentials);
        }

        @Override
        public void abortWith(RestResponse<?> response) {
            GenericRequestSpec.this.abortedResponse = response;
        }
    };
    protected RetryPolicy requestRetryPolicy;
    private boolean localInterceptorsSorted = true;
    private boolean serializerOverridden = false;

    protected GenericRequestSpec(BaseUrlRegistryPool baseUrlRegistryPool, String uriPath,
                                 HttpMethod method,
                                 SerializerRegistry<MessageSerializer> serializerRegistry) {
        this.baseUrlRegistryPool = baseUrlRegistryPool;
        this.uriPath = uriPath;
        this.method = method;
        this.serializerRegistry = serializerRegistry;
        urlPool = DefaultUrlPool.DEFAULT;
        messageSerializer = updateSerializerFromPool(urlPool);
        requestRetryPolicy = baseUrlRegistryPool.getRetryPolicy(urlPool);
    }

    @Override
    public RequestSpec pool(UrlPool urlPool) {
        this.urlPool = urlPool;
        if (!serializerOverridden) {
            messageSerializer = updateSerializerFromPool(urlPool);
        }
        if (requestRetryPolicy == null) {
            requestRetryPolicy = baseUrlRegistryPool.getRetryPolicy(urlPool);
        }
        return this;
    }

    @Override
    public RequestSpec unsafeHeader(HttpHeaderName name, String value) {
        headers.put(name.getCode(), value);
        return this;
    }

    @Override
    public RequestSpec queryParam(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    @Override
    public RequestSpec formParam(String name, String value) {
        formParams.put(name, value);
        return this;
    }

    @Override
    public RequestSpec body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public RequestSpec serializer(SerializerFormat format) {
        messageSerializer = serializerRegistry.get(format);
        serializerOverridden = true;
        return this;
    }

    @Override
    public RequestSpec timeout(Duration timeout) {
        this.requestTimeout = timeout;
        return this;
    }

    @Override
    public RequestSpec interceptor(RequestInterceptor interceptor) {
        interceptors.add(interceptor);
        localInterceptorsSorted = false;
        return this;
    }

    @Override
    public RequestSpec retry(int maxRetries, Duration backoffMs) {
        requestRetryPolicy = RetryPolicy.withSafeMethods(maxRetries + 1, backoffMs);
        return this;
    }

    @Override
    public RequestSpec retry(int maxRetries, Duration backoffMs, Duration maxBackoffMs) {
        requestRetryPolicy = RetryPolicy.withSafeMethods(maxRetries + 1, backoffMs, maxBackoffMs);
        return this;
    }

    @Override
    public RequestSpec disableRetry() {
        requestRetryPolicy = RetryPolicy.none();
        return this;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String getUriPath() {
        return uriPath;
    }

    @Override
    public UrlPool getPool() {
        return urlPool;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public Map<String, String> getFormParams() {
        return formParams;
    }

    @Override
    public Object getBody() {
        return body;
    }

    @Override
    public final <T> RestResponse<T> send(Class<T> responseType) {
        abortedResponse = null;
        List<RequestInterceptor> poolInterceptors = Collections.emptyList();
        if (urlPool != null) {
            poolInterceptors = baseUrlRegistryPool.getInterceptors(urlPool);
        }
        if (!localInterceptorsSorted && !interceptors.isEmpty()) {
            interceptors.sort(Ordered.COMPARATOR);
            localInterceptorsSorted = true;
        }
        log.trace("Executing interceptors for {} {} (pool: {})", method, uriPath,
            urlPool != null ? urlPool.getPoolName() : "none");
        CollectionUtil.consumeMergedSorted(
            poolInterceptors,
            interceptors,
            Ordered.COMPARATOR,
            interceptor -> {
                log.trace("Running interceptor: {}", interceptor.getClass().getSimpleName());
                interceptor.intercept(reusableContext);
                if (abortedResponse != null) {
                    log.debug("Request aborted by interceptor: {} for {} {}",
                        interceptor.getClass().getSimpleName(), method, uriPath);
                }
                return abortedResponse == null;
            }
        );
        if (abortedResponse != null) {
            return CastUtil.unsafeCast(abortedResponse);
        }
        try {
            log.debug("Starting request execution: {} {} (retry policy: {})", method, uriPath,
                requestRetryPolicy.getClass().getSimpleName());
            return RetryExecutor.executeSync(
                () -> onSend(responseType),
                method,
                requestRetryPolicy,
                (attempt, response) -> {
                    final boolean retryable = isRetryableStatus(response.getStatus());
                    if (retryable) {
                        log.debug("Received retryable status code: {} for {} {}",
                            response.getStatus(), method, uriPath);
                    }
                    return retryable;
                },
                (attempt, ex) -> {
                    log.debug("Request exception encountered (attempt {}): {}", attempt,
                        ex.getMessage());
                    return true; // retryableErr
                }
            );
        } catch (Exception ex) {
            if (ex instanceof RestRequestException) {
                throw (RestRequestException) ex;
            }
            log.error("Request failed definitively: {} {} - {}", method, uriPath, ex.getMessage());
            throw new RestRequestException(String.format("Request failed: %s %s", method.name(),
                uriPath), ex);
        }
    }

    protected abstract <T> RestResponse<T> onSend(Class<T> responseType);

    protected String resolveFullUri() {
        if (NetworkUtil.isAbsoluteUrl(uriPath)) {
            return uriPath;
        }
        final String baseUrl = baseUrlRegistryPool.getUrl(urlPool);
        if (baseUrl == null) {
            throw new IllegalStateException("Base URL not found in registry for pool: " + urlPool);
        }
        return NetworkUtil.concatPaths(baseUrl, uriPath);
    }

    protected <T> T parseResponseBody(byte[] responseBytes, Class<T> responseType) {
        T parsedBody = null;
        if (responseType != Void.class && responseBytes != null && responseBytes.length > 0) {
            parsedBody = messageSerializer.deserializeFromBytes(responseBytes, responseType);
        }
        return parsedBody;
    }

    private MessageSerializer updateSerializerFromPool(UrlPool pool) {
        return serializerRegistry.get(baseUrlRegistryPool.getFormat(pool));
    }

    private boolean isRetryableStatus(HttpStatus status) {
        final int code = status.getCode();
        return code >= 500 || code == 429;
    }
}
