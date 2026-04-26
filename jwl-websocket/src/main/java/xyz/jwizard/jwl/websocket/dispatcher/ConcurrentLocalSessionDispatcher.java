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
package xyz.jwizard.jwl.websocket.dispatcher;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.registry.WsSessionRegistry;

public class ConcurrentLocalSessionDispatcher implements LocalSessionDispatcher {
    private static final Logger LOG = LoggerFactory
        .getLogger(ConcurrentLocalSessionDispatcher.class);

    private final WsSessionRegistry registry;
    private final ExecutorService executorService;

    private ConcurrentLocalSessionDispatcher(WsSessionRegistry registry,
                                             ExecutorService executorService) {
        this.registry = registry;
        this.executorService = executorService;
    }

    public static ConcurrentLocalSessionDispatcher createVirtual(WsSessionRegistry registry) {
        return new ConcurrentLocalSessionDispatcher(registry,
            Executors.newVirtualThreadPerTaskExecutor());
    }

    @Override
    public void dispatchRaw(String topic, byte[] payload) {
        broadcast(registry.getUnsafeSubscribers(topic), topic, payload);
    }

    @Override
    public void dispatchRawAll(byte[] payload) {
        broadcast(registry.getAllSessions(), null, payload);
    }

    private void broadcast(Collection<WsSession> sessions, String topic, byte[] payload) {
        if (sessions.isEmpty()) {
            return;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Broadcasting RAW payload to {} sessions (topic: {})", sessions.size(),
                topic != null ? topic : "GLOBAL");
        }
        for (WsSession session : sessions) {
            executorService.submit(() -> send(session, payload));
        }
    }

    private void send(WsSession session, byte[] payload) {
        if (session.isClosed()) {
            return;
        }
        try {
            session.sendAdapted(payload);
        } catch (Exception ex) {
            LOG.warn("Send failed for session {}, removing. Reason: {}", session.getSessionId(),
                ex.getMessage());
            registry.unregister(session);
        }
    }
}
