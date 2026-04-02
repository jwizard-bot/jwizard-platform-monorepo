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
package xyz.jwizard.jwl.common;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ModuleIdentityTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void shouldIdentifyModule() {
        // given
        final String moduleName = getModuleName();
        final String packageName = getClass().getPackageName();
        // when
        log.info("running smoke test for module: {} (package: {})", moduleName, packageName);
        // then
        assertThat(moduleName).isNotBlank();
        assertThat(packageName).startsWith("xyz.jwizard.jwl");
    }

    protected abstract String getModuleName();
}
