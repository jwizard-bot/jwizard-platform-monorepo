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
package xyz.jwizard.jwl.codec.envelope;

public interface OpCode {
    static int combine(int category, int action) {
        return (category << 16) | (action & 0xFF);
    }

    int getCode();

    String getName();

    default String asString() {
        final int currentCode = getCode();
        final int category = (currentCode >> 16) & 0xFF;
        final int action = currentCode & 0xFF;
        return String.format("%s (0x%02X:%02X) [%d]", getName(), category, action, currentCode);
    }
}
