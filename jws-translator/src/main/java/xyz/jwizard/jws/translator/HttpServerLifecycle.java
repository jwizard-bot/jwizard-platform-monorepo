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
package xyz.jwizard.jws.translator;

import java.util.Set;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.serialization.json.JacksonSerializer;
import xyz.jwizard.jwl.common.serialization.raw.RawByteSerializer;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.http.jetty.JettyHttpServer;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class HttpServerLifecycle implements LifecycleHook {
    private final HttpServer httpServer;

    @Inject
    HttpServerLifecycle(ComponentProvider componentProvider) {
        httpServer = JettyHttpServer.builder()
            .componentProvider(componentProvider)
            .serializerRegistry(SerializerRegistry.createDefault()
                .register(JacksonSerializer.createDefaultStrictMapper())
                .register(RawByteSerializer.createDefault())
            )
            .ignoredPaths(Set.of())
            .port(9094) /*TODO: incoming from config server*/
            .build();
    }

    @Override
    public void onStart(ComponentProvider componentProvider, ClassScanner scanner) {
        httpServer.start();
    }

    @Override
    public void onStop() {
        httpServer.close();
    }
}
