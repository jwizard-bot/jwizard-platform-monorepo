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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsHandshakeRequest;

public class CompositeWsAuthenticator implements WsAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeWsAuthenticator.class);

    private final List<WsAuthenticator> authenticators;

    public CompositeWsAuthenticator(List<WsAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }

    @Override
    public String authenticate(WsHandshakeRequest req) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting composite authentication, evaluating {} authenticators",
                authenticators.size());
        }
        for (final WsAuthenticator authenticator : authenticators) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Evaluating request using: {}", authenticator.getClass().getSimpleName());
            }
            final String principalId = authenticator.authenticate(req);
            if (principalId != null) {
                LOG.debug("Authentication successful via {}, principalId: {}",
                    authenticator.getClass().getSimpleName(), principalId);
                return principalId;
            }
        }
        LOG.debug("Composite authentication failed, " +
            "no authenticator was able to identify the principal");
        return null;
    }
}
