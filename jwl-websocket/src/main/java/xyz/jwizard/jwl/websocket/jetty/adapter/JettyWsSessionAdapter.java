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

import org.eclipse.jetty.websocket.api.Session;

import xyz.jwizard.jwl.codec.UnsupportedDataTypeException;
import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.util.concurrent.ConcurrentUtil;
import xyz.jwizard.jwl.net.ws.GenericWsSessionAdapter;
import xyz.jwizard.jwl.websocket.WsSession;

public class JettyWsSessionAdapter extends GenericWsSessionAdapter implements WsSession {
    private final Session session;
    private final EnvelopeSerializer<?> envelopeSerializer;

    public JettyWsSessionAdapter(Session session, String principalId,
                                 EnvelopeSerializer<?> envelopeSerializer) {
        super(principalId, envelopeSerializer);
        this.session = session;
        this.envelopeSerializer = envelopeSerializer;
    }

    @Override
    protected void onSend(String message) {
        ConcurrentUtil.await(cb -> session.sendText(message, new JettyCallbackAdapter(cb)));
    }

    @Override
    protected void onSend(byte[] message) {
        ConcurrentUtil.await(cb ->
            session.sendBinary(ByteBuffer.wrap(message), new JettyCallbackAdapter(cb))
        );
    }

    @Override
    public void sendAdapted(byte[] payload) {
        if (isClosed()) {
            log.debug("Skipping sendAdapted - session is closed, sessionId: {}", sessionId);
            return;
        }
        try {
            envelopeSerializer.acceptRaw(payload, this);
        } catch (UnsupportedDataTypeException | MessageSerializerException ex) {
            log.error("Message error for RAW adaptation: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error during RAW adaptation, sessionId: {}", sessionId, ex);
        }
    }

    @Override
    protected void onClose(int code, String reason) {
        ConcurrentUtil.await(cb -> session.close(code, reason, new JettyCallbackAdapter(cb)));
    }

    @Override
    public boolean isClosed() {
        return !session.isOpen();
    }
}
