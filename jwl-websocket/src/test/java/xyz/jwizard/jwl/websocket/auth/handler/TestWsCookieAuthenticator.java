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
package xyz.jwizard.jwl.websocket.auth.handler;

import xyz.jwizard.jwl.websocket.TestWsCookie;
import xyz.jwizard.jwl.websocket.auth.WsCookieAuthenticator;

public class TestWsCookieAuthenticator extends WsCookieAuthenticator {
    public TestWsCookieAuthenticator() {
        super(TestWsCookie.SESSION_COOKIE);
    }

    @Override
    protected String validateCookieAndGetPrincipal(String sid) {
        if (sid == null || sid.isBlank()) {
            return null;
        }
        if (sid.startsWith("valid-")) {
            return "user-" + sid.substring(6);
        }
        log.warn("Invalid session cookie attempt: {}", sid);
        return null;
    }
}
