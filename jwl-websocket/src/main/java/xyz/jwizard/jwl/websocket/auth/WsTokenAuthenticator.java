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

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.websocket.WsHandshakeRequest;
import xyz.jwizard.jwl.websocket.header.CommonWsHeader;

public class WsTokenAuthenticator implements WsAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(WsTokenAuthenticator.class);

    private final String expectedToken;
    private final String principalId;
    private final String queryParameterKey;

    private WsTokenAuthenticator(Builder builder) {
        expectedToken = builder.expectedToken;
        principalId = builder.principalId;
        queryParameterKey = builder.queryParameterKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String authenticate(WsHandshakeRequest req) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Attempting token authentication for configured principal: '{}'",
                principalId);
        }
        final String incomingToken = req.getHeader(CommonWsHeader.X_WS_AUTH_TOKEN);
        if (expectedToken.equals(incomingToken)) {
            LOG.trace("Authentication successful via HTTP header for principal: '{}'", principalId);
            return principalId;
        }
        if (queryParameterKey == null) {
            LOG.debug("Authentication failed for principal: '{}', " +
                "header token mismatch and query parameter fallback is disabled", principalId);
            return null;
        }
        LOG.trace("Header token mismatch. Falling back to query parameter check using key: '?{}='",
            queryParameterKey);
        final List<String> tokenParams = req.getQueryParameter(queryParameterKey);
        if (tokenParams == null || tokenParams.isEmpty()) {
            LOG.debug("Authentication failed for principal: '{}', query parameter '{}' is missing",
                principalId, queryParameterKey);
            return null;
        }
        if (expectedToken.equals(tokenParams.getFirst())) {
            LOG.trace("Authentication successful via query parameter '{}' for principal: '{}'",
                queryParameterKey, principalId);
            return principalId;
        }
        LOG.debug("Authentication failed for principal: '{}', query parameter token mismatch",
            principalId);
        return null;
    }

    public static class Builder {
        private String expectedToken;
        private String principalId;
        private String queryParameterKey = null;

        private Builder() {
        }

        public Builder expectedToken(String expectedToken) {
            this.expectedToken = expectedToken;
            return this;
        }

        public Builder principalId(String principalId) {
            this.principalId = principalId;
            return this;
        }

        public Builder withQueryParameterCheck(@Nullable String queryParameterKey) {
            this.queryParameterKey = queryParameterKey;
            return this;
        }

        public WsTokenAuthenticator build() {
            Assert.notNull(expectedToken, "ExpectedToken cannot be null");
            Assert.notNull(principalId, "PrincipalId cannot be null");
            return new WsTokenAuthenticator(this);
        }
    }
}
