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
package xyz.jwizard.jws.ingestor;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jws.ingestor.config.scripting.IngestorScript;
import xyz.jwizard.jws.ingestor.scripting.JsEngine;
import xyz.jwizard.jws.ingestor.scripting.graal.GraalJsEngine;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
class JsEngineLifecycle implements LifecycleHook {
    private final GraalJsEngine jsEngine;

    JsEngineLifecycle() {
        jsEngine = GraalJsEngine.builder()
            .withLibrary(IngestorScript.YARN_PARSER)
            .build();
    }

    @Override
    public void onStart(ComponentProvider componentProvider, ClassScanner scanner) {
        jsEngine.start();
    }

    @Override
    public void onStop() {
        jsEngine.close();
    }

    @Produces
    @Singleton
    JsEngine jsEngine() {
        return jsEngine;
    }
}
