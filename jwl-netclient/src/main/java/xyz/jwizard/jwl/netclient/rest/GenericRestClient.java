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
package xyz.jwizard.jwl.netclient.rest;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.codec.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.bootstrap.lifecycle.IdempotentService;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.CastUtil;
import xyz.jwizard.jwl.netclient.rest.pool.BaseUrlRegistryPool;
import xyz.jwizard.jwl.netclient.rest.pool.DefaultUrlPool;
import xyz.jwizard.jwl.netclient.rest.pool.PoolConfig;
import xyz.jwizard.jwl.netclient.rest.pool.UrlPool;

public abstract class GenericRestClient extends IdempotentService implements RestClient {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseUrlRegistryPool baseUrlRegistryPool;
    protected final Duration connectTimeout;
    protected final boolean followRedirects;
    protected final int maxRedirects;
    protected final SerializerRegistry<MessageSerializer> serializerRegistry;
    protected final ClassScanner scanner;

    protected GenericRestClient(AbstractBuilder<?> builder) {
        baseUrlRegistryPool = builder.baseUrlRegistryPool;
        connectTimeout = builder.connectTimeout;
        followRedirects = builder.followRedirects;
        maxRedirects = builder.maxRedirects;
        serializerRegistry = builder.serializerRegistry;
        scanner = builder.scanner;
    }

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>> {
        private final BaseUrlRegistryPool baseUrlRegistryPool = new BaseUrlRegistryPool();
        private Duration connectTimeout = Duration.ofMinutes(1);
        private boolean followRedirects = true;
        private int maxRedirects = 8;
        private SerializerRegistry<MessageSerializer> serializerRegistry;
        private ClassScanner scanner;

        protected AbstractBuilder() {
        }

        protected B self() {
            return CastUtil.unsafeCast(this);
        }

        public B defaultPool(String baseUrl, PoolConfig config) {
            baseUrlRegistryPool.register(DefaultUrlPool.DEFAULT, baseUrl, config);
            return self();
        }

        public B pool(UrlPool urlPool, String baseUrl, PoolConfig config) {
            baseUrlRegistryPool.register(urlPool, baseUrl, config);
            return self();
        }

        public B connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return self();
        }

        public B followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return self();
        }

        public B maxRedirects(int maxRedirects) {
            this.maxRedirects = maxRedirects;
            return self();
        }

        public B serializerRegistry(SerializerRegistry<MessageSerializer> serializerRegistry) {
            this.serializerRegistry = serializerRegistry;
            return self();
        }

        public B scanner(ClassScanner scanner) {
            this.scanner = scanner;
            return self();
        }

        protected void validate() {
            Assert.notNull(connectTimeout, "ConnectTimeout cannot be null");
            Assert.state(maxRedirects > 0, "MaxRedirects must be greater than zero");
            Assert.notNull(serializerRegistry, "SerializerRegistry cannot be null");
            Assert.notNull(scanner, "ClassScanner cannot be null");
        }

        public abstract GenericRestClient build();
    }
}
