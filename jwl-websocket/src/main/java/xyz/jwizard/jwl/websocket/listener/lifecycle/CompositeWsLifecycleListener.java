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
package xyz.jwizard.jwl.websocket.listener.lifecycle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsSession;

public class CompositeWsLifecycleListener implements WsLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeWsLifecycleListener.class);

    private final List<WsLifecycleListener> lifecycleListeners;

    public CompositeWsLifecycleListener(List<WsLifecycleListener> lifecycleListeners) {
        this.lifecycleListeners = lifecycleListeners;
    }

    @Override
    public void onConnect(WsSession session) {
        LOG.debug("Session {} connecting to pipeline ({} listeners)", session.getSessionId(),
            lifecycleListeners.size());
        for (final WsLifecycleListener listener : lifecycleListeners) {
            listener.onConnect(session);
        }
    }

    @Override
    public void onClose(WsSession session, int statusCode, String reason) {
        LOG.debug("Session {} closing (code: {}, reason: {}), notifying pipeline",
            session.getSessionId(), statusCode, reason);
        for (final WsLifecycleListener listener : lifecycleListeners) {
            listener.onClose(session, statusCode, reason);
        }
    }

    @Override
    public void onError(WsSession session, Throwable cause) {
        LOG.debug("Error in session {}: {}, notifying pipeline", session.getSessionId(),
            cause.getMessage());
        for (final WsLifecycleListener listener : lifecycleListeners) {
            listener.onError(session, cause);
        }
    }
}
