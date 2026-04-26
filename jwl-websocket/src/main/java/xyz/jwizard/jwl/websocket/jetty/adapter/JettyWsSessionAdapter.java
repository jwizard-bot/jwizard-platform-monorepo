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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.envelope.EncodedPayloadVisitor;
import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.MessageEnvelope;
import xyz.jwizard.jwl.codec.envelope.OpCode;
import xyz.jwizard.jwl.codec.envelope.UnsupportedEnvelopeDataTypeException;
import xyz.jwizard.jwl.codec.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.util.concurrent.ConcurrentUtil;
import xyz.jwizard.jwl.websocket.WsSession;

public class JettyWsSessionAdapter implements WsSession, EncodedPayloadVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(JettyWsSessionAdapter.class);

    private final Session session;
    private final String principalId;
    private final EnvelopeSerializer<?> envelopeSerializer;
    private final String sessionId;

    public JettyWsSessionAdapter(Session session, String principalId,
                                 EnvelopeSerializer<?> envelopeSerializer) {
        this.session = session;
        this.principalId = principalId;
        this.envelopeSerializer = envelopeSerializer;
        sessionId = UUID.randomUUID().toString();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    @Override
    public void send(byte[] message) {
        sendSafely(
            () -> LOG.trace("Sending binary message, sessionId: {}, size: {} bytes", sessionId,
                message.length),
            cb -> session.sendBinary(ByteBuffer.wrap(message), cb)
        );
    }

    @Override
    public void send(String message) {
        sendSafely(
            () -> LOG.trace("Sending text message, sessionId: {}, size: {} chars", sessionId,
                message.length()),
            cb -> session.sendText(message, cb)
        );
    }

    @Override
    public void sendEnvelope(OpCode opCode, Object data) {
        final String opName = opCode != null ? opCode.getName() : "UNKNOWN";
        if (isClosed()) {
            LOG.debug("Skipping send - session is closed, sessionId: {}, OP: {}", sessionId, opName);
            return;
        }
        LOG.debug("Preparing {} envelope, sessionId: {}, OP: {} (ID: {})",
            envelopeSerializer.format().getFormat(), sessionId, opName,
            opCode != null ? opCode.getCode() : "null");
        try {
            envelopeSerializer.serializeAndAccept(opCode, data, this);
        } catch (UnsupportedEnvelopeDataTypeException | MessageSerializerException ex) {
            LOG.error("Message error for {}: {}", envelopeSerializer.format().getFormat(),
                ex.getMessage());
        } catch (Exception ex) {
            LOG.error("Unexpected error during processing, sessionId: {}, OP: {}", sessionId,
                opName, ex);
        }
    }

    @Override
    public void sendAdapted(byte[] payload) {
        if (isClosed()) {
            LOG.debug("Skipping sendAdapted - session is closed, sessionId: {}", sessionId);
            return;
        }
        try {
            envelopeSerializer.acceptRaw(payload, this);
        } catch (UnsupportedEnvelopeDataTypeException | MessageSerializerException ex) {
            LOG.error("Message error for RAW adaptation: {}", ex.getMessage());
        } catch (Exception ex) {
            LOG.error("Unexpected error during RAW adaptation, sessionId: {}", sessionId, ex);
        }
    }

    @Override
    public MessageEnvelope<?> unwrap(byte[] payload, Function<Integer, Class<?>> typeResolver) {
        return envelopeSerializer.deserializeEnvelope(payload, typeResolver);
    }

    @Override
    public MessageEnvelope<?> unwrap(String payload, Function<Integer, Class<?>> typeResolver) {
        return envelopeSerializer.deserializeEnvelope(payload, typeResolver);
    }

    @Override
    public void close(int statusCode, String reason) {
        if (isClosed()) {
            return;
        }
        LOG.debug("Initiating session closure, sessionId: {}, status: {}, reason: '{}'", sessionId,
            statusCode, reason);
        ConcurrentUtil.await(cb -> session.close(statusCode, reason, new JettyCallbackAdapter(cb)));
    }

    @Override
    public boolean isClosed() {
        return !session.isOpen();
    }

    private void sendSafely(Runnable logAction, Consumer<Callback> jettyAction) {
        if (isClosed()) {
            LOG.trace("Skipping send - session is closed, sessionId: {}", sessionId);
            return;
        }
        if (LOG.isTraceEnabled()) {
            logAction.run();
        }
        try {
            ConcurrentUtil.await(futureCallback ->
                jettyAction.accept(new JettyCallbackAdapter(futureCallback))
            );
        } catch (Exception ex) {
            LOG.error("Transport layer failed to deliver message for session {}: {}", sessionId,
                ex.getMessage());
        }
    }

    @Override
    public final void accept(byte[] payload) {
        send(payload);
    }

    @Override
    public final void accept(String payload) {
        send(payload);
    }
}
