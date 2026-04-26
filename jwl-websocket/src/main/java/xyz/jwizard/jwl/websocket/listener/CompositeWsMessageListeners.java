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
package xyz.jwizard.jwl.websocket.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.listener.action.pool.WsActionPool;

public class CompositeWsMessageListeners extends WsMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeWsMessageListeners.class);

    private final List<WsMessageListener> messageListeners;

    public CompositeWsMessageListeners(List<WsMessageListener> messageListeners) {
        super((WsActionPool) null);
        this.messageListeners = messageListeners;
    }

    @Override
    public void onMessage(WsSession session, byte[] message) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Propagating binary message ({} bytes) from session {} through pipeline",
                message.length, session.getSessionId());
        }
        for (final WsMessageListener listener : messageListeners) {
            try {
                listener.onMessage(session, message);
            } catch (Exception ex) {
                LOG.error("Message listener {} failed to process binary message",
                    listener.getClass().getSimpleName(), ex);
            }
        }
    }

    @Override
    public void onMessage(WsSession session, String message) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Propagating text message (length: {}) from session {} through pipeline",
                message.length(), session.getSessionId());
        }
        for (final WsMessageListener listener : messageListeners) {
            try {
                listener.onMessage(session, message);
            } catch (Exception ex) {
                LOG.error("Message listener {} failed to process text message",
                    listener.getClass().getSimpleName(), ex);
            }
        }
    }
}
