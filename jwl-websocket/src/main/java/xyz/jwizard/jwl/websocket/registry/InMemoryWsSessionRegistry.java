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
package xyz.jwizard.jwl.websocket.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.broadcast.WsTopic;

public class InMemoryWsSessionRegistry implements WsSessionRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryWsSessionRegistry.class);

    private final Map<String, WsSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, WsSession>> subscriptions = new ConcurrentHashMap<>();
    // for optimization
    private final Map<String, Set<String>> sessionTopics = new ConcurrentHashMap<>();

    private InMemoryWsSessionRegistry() {
    }

    public static InMemoryWsSessionRegistry createDefault() {
        return new InMemoryWsSessionRegistry();
    }

    @Override
    public void register(WsSession session) {
        activeSessions.put(session.getSessionId(), session);
        LOG.debug("Session registered: {} (total active: {})", session.getSessionId(),
            activeSessions.size());
    }

    @Override
    public void unregister(WsSession session) {
        final String sid = session.getSessionId();
        activeSessions.remove(sid);
        final Set<String> topics = sessionTopics.remove(sid);
        final int topicsCount = (topics != null) ? topics.size() : 0;
        if (topics == null) {
            return;
        }
        for (final String topic : topics) {
            subscriptions.computeIfPresent(topic, (t, subs) -> {
                subs.remove(sid);
                if (subs.isEmpty()) {
                    LOG.trace("Topic '{}' became empty and was removed during unregister of {}",
                        t, sid);
                    return null;
                }
                return subs;
            });
        }
        LOG.debug("Session unregistered: {} (was in {} topics, remaining sessions: {})",
            sid, topicsCount, activeSessions.size());
    }

    @Override
    public Collection<WsSession> getUnsafeSubscribers(String topic) {
        final Map<String, WsSession> subs = subscriptions.get(topic);
        if (subs == null) {
            LOG.trace("No subscribers found for topic: {}", topic);
            return Collections.emptyList();
        }
        return subs.values();
    }

    @Override
    public void subscribe(WsSession session, WsTopic topic) {
        final String sid = session.getSessionId();
        final String topicName = topic.getTopic();
        subscriptions.computeIfAbsent(topicName, k -> {
            LOG.trace("Creating new topic bucket: {}", k);
            return new ConcurrentHashMap<>();
        }).put(sid, session);
        sessionTopics.computeIfAbsent(sid, k -> ConcurrentHashMap.newKeySet()).add(topicName);
        LOG.debug("Session {} subscribed to '{}'", sid, topicName);
    }

    @Override
    public void unsubscribe(WsSession session, WsTopic topic) {
        final String sid = session.getSessionId();
        final String topicName = topic.getTopic();
        subscriptions.computeIfPresent(topicName, (t, subs) -> {
            subs.remove(sid);
            if (subs.isEmpty()) {
                LOG.trace("Topic '{}' removed because last subscriber {} left", t, sid);
                return null;
            }
            return subs;
        });
        sessionTopics.computeIfPresent(sid, (s, topics) -> {
            topics.remove(topicName);
            return topics.isEmpty() ? null : topics;
        });
        LOG.debug("Session {} unsubscribed from '{}'", sid, topicName);
    }

    @Override
    public Collection<WsSession> getSubscribers(WsTopic topic) {
        return getUnsafeSubscribers(topic.getTopic());
    }

    @Override
    public Collection<WsSession> getAllSessions() {
        return activeSessions.values();
    }
}
