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
package xyz.jwizard.jws.ingestor.scripting.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xyz.jwizard.jwl.common.bootstrap.lifecycle.IdempotentService;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jws.ingestor.scripting.JsEngine;
import xyz.jwizard.jws.ingestor.scripting.ScriptFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraalJsEngine extends IdempotentService implements JsEngine {
    private final List<ScriptFile> librariesToPreload;

    private Context context;

    private GraalJsEngine(Builder builder) {
        librariesToPreload = builder.libraries;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void onStart() throws Exception {
        LOG.info("Initializing GraalVM JS context, libraries to preload: {}",
            librariesToPreload.size());
        context = Context.newBuilder("js")
            .allowAllAccess(false)
            .build();
        for (final ScriptFile scriptFile : librariesToPreload) {
            LOG.debug("Preloading library: {}", scriptFile.getKey());
            final long start = System.currentTimeMillis();
            context.eval(buildSourceFromResource(scriptFile));
            LOG.trace("Library '{}' loaded in {} ms", scriptFile.getKey(),
                (System.currentTimeMillis() - start));
        }
        LOG.info("GraalVM JS context initialized successfully");
    }

    @Override
    protected void onStop() {
        IoUtil.closeQuietly(context, Context::close);
    }

    @Override
    public <T> T executeScript(ScriptFile scriptFile, Map<String, Object> variables,
                               Class<T> returnType) throws IOException {
        ensureRunning();
        LOG.debug("Executing script: '{}' with {} injected variables, expected return type: {}",
            scriptFile.getKey(), variables.size(), returnType.getSimpleName());
        if (LOG.isTraceEnabled()) {
            LOG.trace("Injected variable keys: {}", variables.keySet());
        }
        final Value bindings = context.getBindings("js");
        for (final Map.Entry<String, Object> entry : variables.entrySet()) {
            bindings.putMember(entry.getKey(), entry.getValue());
        }
        try {
            return doExecuteScript(scriptFile).as(returnType);
        } finally {
            for (final String key : variables.keySet()) {
                bindings.removeMember(key);
            }
            LOG.trace("Cleaned up injected variables for script: '{}'", scriptFile.getKey());
        }
    }

    @Override
    public <T> T executeScript(ScriptFile scriptFile, Class<T> returnType) throws IOException {
        LOG.debug("Executing script: '{}'. Expected return type: {}", scriptFile.getKey(),
            returnType.getSimpleName());
        return doExecuteScript(scriptFile).as(returnType);
    }

    @Override
    public void executeScript(ScriptFile scriptFile) throws IOException {
        LOG.debug("Executing script (fire-and-forget): '{}'", scriptFile.getKey());
        doExecuteScript(scriptFile);
    }

    @Override
    public <T> T callFunction(String functionName, Class<T> returnType, Object... args) {
        LOG.debug("Calling JS function: '{}' with {} arguments, expected return type: {}",
            functionName, args.length, returnType.getSimpleName());
        return doCallFunction(functionName, args).as(returnType);
    }

    @Override
    public void callFunction(String functionName, Object... args) {
        LOG.debug("Calling JS function (fire-and-forget): '{}' with {} arguments", functionName,
            args.length);
        doCallFunction(functionName, args);
    }

    private Value doExecuteScript(ScriptFile scriptFile) throws IOException {
        ensureRunning();
        LOG.trace("Evaluating source for script: '{}'", scriptFile.getKey());
        final long start = System.currentTimeMillis();
        final Value result = context.eval(buildSourceFromResource(scriptFile));
        LOG.trace("Script '{}' executed in {} ms", scriptFile.getKey(),
            (System.currentTimeMillis() - start));
        return result;
    }

    private Value doCallFunction(String functionName, Object... args) {
        ensureRunning();
        final Value function = context.getBindings("js").getMember(functionName);
        if (function == null || !function.canExecute()) {
            throw new IllegalArgumentException("Function '" + functionName + "' does not exist");
        }
        final long start = System.currentTimeMillis();
        final Value result = function.execute(args);
        LOG.trace("Function '{}' executed in {} ms", functionName,
            (System.currentTimeMillis() - start));
        return result;
    }

    private void ensureRunning() {
        Objects.requireNonNull(context, "JsEngine is not running");
    }

    private Source buildSourceFromResource(ScriptFile scriptFile) throws IOException {
        final URL resourceUrl = IoUtil.getRequiredResourceUrl(scriptFile.getKey());
        return Source.newBuilder("js", resourceUrl)
            .name(resourceUrl.getPath())
            .build();
    }

    public static class Builder {
        private final List<ScriptFile> libraries = new ArrayList<>();

        private Builder() {
        }

        public Builder withLibrary(ScriptFile ScriptFile) {
            this.libraries.add(ScriptFile);
            return this;
        }

        public GraalJsEngine build() {
            return new GraalJsEngine(this);
        }
    }
}
