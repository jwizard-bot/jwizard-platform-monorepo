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
package xyz.jwizard.jwl.websocket.listener.action;

import xyz.jwizard.jwl.codec.envelope.OpCode;

public enum WsOpCode implements OpCode {
    RATE_LIMIT_EXCEEDED(0x01, 0x01),    // 65537 (0x010001)
    UNKNOWN_ACTION(0x01, 0x02),         // 65538 (0x010002)
    INVALID_PAYLOAD(0x01, 0x03),        // 65539 (0x010003)
    INTERNAL_ERROR(0x01, 0x04),         // 65540 (0x010004)
    HEARTBEAT(0x01, 0x05),              // 65541 (0x010005)
    ;

    private final int code;

    WsOpCode(int category, int action) {
        code = OpCode.combine(category, action);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return asString();
    }
}
