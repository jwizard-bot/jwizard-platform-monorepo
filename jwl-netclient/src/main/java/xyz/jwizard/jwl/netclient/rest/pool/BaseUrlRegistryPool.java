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
package xyz.jwizard.jwl.netclient.rest.pool;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.SerializerFormat;
import xyz.jwizard.jwl.codec.serialization.StandardSerializerFormat;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.netclient.rest.intercept.RequestInterceptor;
import xyz.jwizard.jwl.netclient.rest.retry.RetryPolicy;

public class BaseUrlRegistryPool {
    private static final Logger LOG = LoggerFactory.getLogger(BaseUrlRegistryPool.class);

    private final Map<String, String> urls = new HashMap<>();
    private final Map<String, PoolConfig> configs = new HashMap<>();

    public void register(UrlPool urlPool, String baseUrl, PoolConfig config) {
        final String normalizedBaseUrl = IoUtil.removeTrailingSlash(baseUrl);
        final String poolName = urlPool.getPoolName();

        LOG.info("Registering URL pool: {} -> {} (interceptors: {}, retry: {})",
            poolName, normalizedBaseUrl, config.getInterceptors().length,
            config.getRetryPolicy().isEnabled() ? "enabled" : "disabled");

        urls.put(poolName, normalizedBaseUrl);
        configs.put(poolName, config);
    }

    public String getUrl(UrlPool urlPool) {
        final String url = urls.get(urlPool.getPoolName());
        if (url == null) {
            LOG.warn("Requested URL for unknown pool: {}", urlPool.getPoolName());
        }
        return url;
    }

    public SerializerFormat getFormat(UrlPool urlPool) {
        final PoolConfig config = configs.get(urlPool.getPoolName());
        return (config != null) ? config.getDefaultFormat() : StandardSerializerFormat.JSON;
    }

    public List<RequestInterceptor> getInterceptors(UrlPool urlPool) {
        final PoolConfig config = configs.get(urlPool.getPoolName());
        if (config == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(config.getInterceptors());
    }

    public RetryPolicy getRetryPolicy(UrlPool urlPool) {
        final PoolConfig config = configs.get(urlPool.getPoolName());
        if (config == null) {
            return RetryPolicy.none();
        }
        return config.getRetryPolicy();
    }
}
