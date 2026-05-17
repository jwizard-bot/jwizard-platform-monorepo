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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import xyz.jwizard.jwl.common.registry.GenericConcurrentRegistry;
import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.broadcast.WsTopic;

public class InMemoryWsSessionRegistry extends GenericConcurrentRegistry<String, WsSession>
    implements WsSessionRegistry {
    // for optimization
    private final Map<String, Map<String, WsSession>> subscriptions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionTopics = new ConcurrentHashMap<>();

    private InMemoryWsSessionRegistry() {
        super();
    }

    public static InMemoryWsSessionRegistry createDefault() {
        return new InMemoryWsSessionRegistry();
    }

    @Override
    public void register(WsSession session) {
        super.register(session.getSessionId(), session);
        log.debug("Session registered: {} (total active: {})", session.getSessionId(),
            getEntries().size());
    }

    @Override
    public void unregister(WsSession session) {
        final String sid = session.getSessionId();
        super.remove(sid);
        final Set<String> topics = sessionTopics.remove(sid);
        final int topicsCount = (topics != null) ? topics.size() : 0;
        if (topics == null) {
            return;
        }
        for (final String topic : topics) {
            subscriptions.computeIfPresent(topic, (t, subs) -> {
                subs.remove(sid);
                if (subs.isEmpty()) {
                    log.trace("Topic '{}' became empty and was removed during unregister of {}", t,
                        sid);
                    return null;
                }
                return subs;
            });
        }
        log.debug("Session unregistered: {} (was in {} topics, remaining sessions: {})", sid,
            topicsCount, getEntries().size());
    }

    @Override
    public Collection<WsSession> getUnsafeSubscribers(String topic) {
        final Map<String, WsSession> subs = subscriptions.get(topic);
        return (subs == null) ? List.of() : subs.values();
    }

    @Override
    public void subscribe(WsSession session, WsTopic topic) {
        final String sid = session.getSessionId();
        final String topicName = topic.getTopic();
        subscriptions.computeIfAbsent(topicName, k -> {
            log.trace("Creating new topic bucket: {}", k);
            return new ConcurrentHashMap<>();
        }).put(sid, session);
        sessionTopics.computeIfAbsent(sid, k -> ConcurrentHashMap.newKeySet()).add(topicName);
        log.debug("Session {} subscribed to '{}'", sid, topicName);
    }

    @Override
    public void unsubscribe(WsSession session, WsTopic topic) {
        final String sid = session.getSessionId();
        final String topicName = topic.getTopic();
        subscriptions.computeIfPresent(topicName, (t, subs) -> {
            subs.remove(sid);
            if (subs.isEmpty()) {
                log.trace("Topic '{}' removed because last subscriber {} left", t, sid);
                return null;
            }
            return subs;
        });
        sessionTopics.computeIfPresent(sid, (s, topics) -> {
            topics.remove(topicName);
            return topics.isEmpty() ? null : topics;
        });
        log.debug("Session {} unsubscribed from '{}'", sid, topicName);
    }

    @Override
    public Collection<WsSession> getSubscribers(WsTopic topic) {
        return getUnsafeSubscribers(topic.getTopic());
    }

    @Override
    public Collection<WsSession> getAllSessions() {
        return super.getAll();
    }
}
