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
package xyz.jwizard.jwl.net.ws;

import xyz.jwizard.jwl.common.util.StringUtil;
import xyz.jwizard.jwl.net.CloseCode;

public enum WsCloseCode implements CloseCode {
    NORMAL(1000, "Normal closure"),
    REPLACED_SESSION(1000, "Replaced by new session"),
    GOING_AWAY(1001, "Going away"),
    UNSUPPORTED_FRAME_TYPE(1003, "Unsupported frame type"),
    INTERNAL_SERVER_ERROR(1011, "Internal server error"),
    SERVER_OVERLOADED(1011, "Server overloaded"),
    ;

    private static final int MAX_CLOSE_REASON_BYTES = 123;

    private final int code;
    private final String reason;

    WsCloseCode(int code, String reason) {
        this.code = code;
        this.reason = StringUtil.truncateToUtf8Bytes(reason, MAX_CLOSE_REASON_BYTES);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDefaultReason() {
        return reason;
    }
}
