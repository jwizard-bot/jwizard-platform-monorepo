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
package xyz.jwizard.jwl.websocket.jetty.adapter;

import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;

import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.StandardOpCode;
import xyz.jwizard.jwl.common.limit.RateLimiter;
import xyz.jwizard.jwl.net.bus.RawBusListener;
import xyz.jwizard.jwl.net.lifecycle.NetworkSessionLifecycleListener;
import xyz.jwizard.jwl.net.ws.GenericWsListenerHandler;
import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.registry.WsSessionRegistry;

public class JettyWsListenerAdapter extends GenericWsListenerHandler<WsSession>
    implements Session.Listener.AutoDemanding {
    private final WsSessionRegistry registry;
    private final RateLimiter rateLimiter;
    private final EnvelopeSerializer<?> envelopeSerializer;
    private final String principalId;

    public JettyWsListenerAdapter(NetworkSessionLifecycleListener<WsSession> lifecycleListener,
                                  RawBusListener<WsSession> busListener,
                                  WsSessionRegistry registry, RateLimiter rateLimiter,
                                  EnvelopeSerializer<?> envelopeSerializer, String principalId) {
        super(registry, lifecycleListener, busListener);
        this.registry = registry;
        this.rateLimiter = rateLimiter;
        this.envelopeSerializer = envelopeSerializer;
        this.principalId = principalId;
    }

    @Override
    public void onWebSocketOpen(Session session) {
        log.debug("WS connection opening for principal: {}", principalId);
        sessionAdapter = new JettyWsSessionAdapter(session, principalId, envelopeSerializer);
        registry.register(sessionAdapter);
        super.handleConnect();
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason, Callback callback) {
        try {
            super.handleClose(statusCode, reason);
        } finally {
            callback.succeed();
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.handleError(cause);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onText(message);
    }

    @Override
    public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
        super.onBinary(payload,
            () -> completeCallback(callback, null),
            ex -> completeCallback(callback, ex)
        );
    }

    private void completeCallback(Callback callback, Throwable error) {
        if (callback == null) {
            return;
        }
        if (error == null) {
            callback.succeed();
        } else {
            callback.fail(error);
        }
    }

    @Override
    protected void cleanupSession() {
        super.cleanupSession();
        rateLimiter.reset(principalId);
    }

    @Override
    protected boolean checkRateLimit() {
        return rateLimiter.tryAcquire(principalId);
    }

    @Override
    protected void onRateLimitExceeded() {
        sessionAdapter.sendEnvelope(StandardOpCode.RATE_LIMIT_EXCEEDED);
    }

    @Override
    protected void onBusinessError() {
        sessionAdapter.sendEnvelope(StandardOpCode.INTERNAL_ERROR);
    }
}
