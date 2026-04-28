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
package xyz.jwizard.jwl.net.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {
    // 1XX
    CONTINUE_100(100),
    SWITCHING_PROTOCOLS_101(101),
    // 2XX
    OK_200(200),
    CREATED_201(201),
    ACCEPTED_202(202),
    NO_CONTENT_204(204),
    // 3XX
    MOVED_PERMANENTLY_301(301),
    FOUND_302(302),
    NOT_MODIFIED_304(304),
    // 4XX
    BAD_REQUEST_400(400),
    UNAUTHORIZED_401(401),
    FORBIDDEN_403(403),
    NOT_FOUND_404(404),
    METHOD_NOT_ALLOWED_405(405),
    CONFLICT_409(409),
    PAYLOAD_TOO_LARGE_413(413),
    UNSUPPORTED_MEDIA_TYPE_415(415),
    TOO_MANY_REQUESTS_429(429),
    // 5XX
    INTERNAL_SERVER_ERROR_500(500),
    NOT_IMPLEMENTED_501(501),
    BAD_GATEWAY_502(502),
    SERVICE_UNAVAILABLE_503(503),
    GATEWAY_TIMEOUT_504(504),
    // other
    UNKNOWN(0),
    ;

    private static final Map<Integer, HttpStatus> BY_CODE = new HashMap<>();

    static {
        for (final HttpStatus status : values()) {
            BY_CODE.put(status.code, status);
        }
    }

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public static HttpStatus fromCode(int code) {
        return BY_CODE.getOrDefault(code, UNKNOWN);
    }

    public int getCode() {
        return code;
    }
}
