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
package xyz.jwizard.jwl.common.serialization.envelope;

public enum TestOpCode implements OpCode {
    USER_DATA((0x01 << 16) | 0x64, "USER_DATA"),
    HEARTBEAT((0x02 << 16) | 0xC8, "HEARTBEAT"),
    ;

    private final int code;
    private final String name;

    TestOpCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final int category = (code >> 16) & 0xFF;
        final int action = code & 0xFF;
        return String.format("%s (0x%02X:%02X) [%d]", name, category, action, code);
    }
}
