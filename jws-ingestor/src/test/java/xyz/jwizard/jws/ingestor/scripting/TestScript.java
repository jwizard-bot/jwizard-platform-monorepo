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
package xyz.jwizard.jws.ingestor.scripting;

public enum TestScript implements ScriptFile {
    CLEANUP("scripting/test-check-cleanup.js"),
    CHECK_SIDE_EFFECT("scripting/test-check-side-effect.js"),
    EXECUTE("scripting/test-execute.js"),
    PRELOAD("scripting/test-preload.js"),
    SIDE_EFFECT("scripting/test-side-effect.js"),
    VARS("scripting/test-vars.js"),
    ;

    private final String key;

    TestScript(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
