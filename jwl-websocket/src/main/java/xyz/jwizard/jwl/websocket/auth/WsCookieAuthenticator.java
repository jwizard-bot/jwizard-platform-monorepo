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
package xyz.jwizard.jwl.websocket.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsCookie;
import xyz.jwizard.jwl.websocket.WsHandshakeRequest;

public abstract class WsCookieAuthenticator implements WsAuthenticator {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final WsCookie cookie;

    protected WsCookieAuthenticator(WsCookie cookie) {
        this.cookie = cookie;
    }

    @Override
    public String authenticate(WsHandshakeRequest req) {
        final String cookieName = cookie.getCode();
        if (log.isTraceEnabled()) {
            log.trace("Attempting cookie authentication, looking for cookie: '{}'", cookieName);
        }
        final String cookieValue = req.getCookie(cookie);
        if (cookieValue == null) {
            log.debug("Authentication failed: cookie '{}' is missing from the handshake request",
                cookieName);
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Cookie '{}' found, delegating validation to implementation", cookieName);
        }
        final String principalId = validateCookieAndGetPrincipal(cookieValue);
        if (principalId != null) {
            log.debug("Authentication successful via cookie '{}' for principal: '{}'", cookieName,
                principalId);
            return principalId;
        }
        log.debug("Authentication failed: invalid value for cookie '{}'", cookieName);
        return null;
    }

    protected abstract String validateCookieAndGetPrincipal(String sid);
}
