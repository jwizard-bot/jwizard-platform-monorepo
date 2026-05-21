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
package xyz.jwizard.jwl.netclient.websocket;

import xyz.jwizard.jwl.codec.envelope.OpCode;

public enum TestWsOpCode implements OpCode {
    SEND_DATA(0x05, 0x01),
    RECEIVE_DATA(0x05, 0x02),
    SEND_DATA_PROTO(0x06, 0x01),
    RECEIVE_DATA_PROTO(0x06, 0x02),
    ;

    private final int code;

    TestWsOpCode(int category, int action) {
        this.code = OpCode.combine(category, action);
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
