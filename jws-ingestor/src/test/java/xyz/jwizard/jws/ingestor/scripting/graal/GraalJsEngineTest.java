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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jws.ingestor.scripting.TestScript;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GraalJsEngineTest {
    private GraalJsEngine engine;

    @BeforeEach
    void setUp() {
        engine = GraalJsEngine.builder()
            .withLibrary(TestScript.PRELOAD)
            .build();
    }

    @AfterEach
    void tearDown() {
        IoUtil.closeQuietly(engine);
    }

    @Test
    @DisplayName("should throw NullPointerException when using engine before calling start()")
    void shouldThrowWhenUsingEngineBeforeStart() {
        assertThatThrownBy(() -> engine.executeScript(TestScript.EXECUTE, String.class))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("JsEngine is not running");
    }

    @Test
    @DisplayName("should preload configured libraries into global scope on start")
    void shouldPreloadLibrariesOnStart() {
        // given
        engine.start();
        // when
        final Integer result = engine.callFunction("multiply", Integer.class, 3, 4);
        // then
        assertThat(result).isEqualTo(12);
    }

    @Test
    @DisplayName("should execute a simple script and return the expected result")
    void shouldExecuteSimpleScriptAndReturnResult() throws Exception {
        // given
        engine.start();
        // when
        final String result = engine.executeScript(TestScript.EXECUTE, String.class);
        // then
        assertThat(result).isEqualTo("Hello from script");
    }

    @Test
    @DisplayName("should execute script correctly using injected Java variables")
    void shouldExecuteScriptWithInjectedVariables() throws Exception {
        // given
        engine.start();
        final Map<String, Object> vars = Map.of(
            "injectedA", 15,
            "injectedB", 25
        );
        // when
        final Integer result = engine.executeScript(TestScript.VARS, vars, Integer.class);
        // then
        assertThat(result).isEqualTo(40);
    }

    @Test
    @DisplayName("should clean up injected variables from global scope after execution")
    void shouldCleanupInjectedVariablesAfterExecution() throws Exception {
        // given
        engine.start();
        final Map<String, Object> vars = Map.of(
            "injectedA", 10,
            "injectedB", 20
        );
        // when
        engine.executeScript(TestScript.VARS, vars, Integer.class);
        final String typeofInjectedA = engine.executeScript(TestScript.CLEANUP, String.class);
        // then
        assertThat(typeofInjectedA).isEqualTo("undefined");
    }

    @Test
    @DisplayName("should safely clean up variables even if the script throws an exception")
    void shouldCleanupVariablesEvenIfScriptThrowsException() {
        // given
        engine.start();
        final Map<String, Object> vars = Map.of("injectedA", "Will Fail");

        assertThatThrownBy(() -> engine.executeScript(TestScript.VARS, vars, Integer.class))
            .isInstanceOf(Exception.class);
        try {
            final String typeofInjectedA = engine.executeScript(TestScript.CLEANUP, String.class);
            assertThat(typeofInjectedA).isEqualTo("undefined");
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("should correctly call preloaded JS function with provided arguments")
    void shouldCallPreloadedFunctionWithArguments() {
        // given
        engine.start();
        // when
        final Integer result = engine.callFunction("multiply", Integer.class, 5, 4);
        // then
        assertThat(result).isEqualTo(20);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when calling a non-existent JS function")
    void shouldThrowWhenCallingNonExistentFunction() {
        // given
        engine.start();
        // when & then
        assertThatThrownBy(() -> engine.callFunction("nonExistentFunc", String.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not exist");
    }
}
