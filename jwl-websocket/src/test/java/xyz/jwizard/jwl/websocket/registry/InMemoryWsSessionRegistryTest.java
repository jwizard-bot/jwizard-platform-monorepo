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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.broadcast.WsTopic;

@ExtendWith(MockitoExtension.class)
class InMemoryWsSessionRegistryTest {
    private InMemoryWsSessionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = InMemoryWsSessionRegistry.createDefault();
    }

    @Test
    @DisplayName("should register and retrieve active sessions")
    void shouldRegisterSessions() {
        // given
        final WsSession sessionA = createMockSession("session-a");
        final WsSession sessionB = createMockSession("session-b");
        // when
        registry.register(sessionA);
        registry.register(sessionB);
        // then
        final Collection<WsSession> allSessions = registry.getAllSessions();
        assertThat(allSessions)
            .as("Registry should contain exactly 2 registered sessions")
            .hasSize(2)
            .containsExactlyInAnyOrder(sessionA, sessionB);
    }

    @Test
    @DisplayName("should safely handle unregister of unknown session")
    void shouldHandleUnknownUnregister() {
        // given
        final WsSession unknownSession = createMockSession("ghost-session");
        // when (should not throw any exceptions)
        registry.unregister(unknownSession);
        // then
        assertThat(registry.getAllSessions()).isEmpty();
    }

    @Test
    @DisplayName("should correctly manage subscriptions to a topic")
    void shouldManageSubscriptions() {
        // given
        final WsSession session = createMockSession("session-1");
        final WsTopic topicNews = createTopic("topic:news");
        registry.register(session);
        // when: subscribe
        registry.subscribe(session, topicNews);
        // then
        assertThat(registry.getSubscribers(topicNews))
            .as("Session should be present in the topic subscribers list")
            .containsExactly(session);
        // when: unsubscribe
        registry.unsubscribe(session, topicNews);
        // then
        assertThat(registry.getSubscribers(topicNews))
            .as("Topic should have no subscribers after unsubscribe")
            .isEmpty();
    }

    @Test
    @DisplayName("unregistering a session should automatically remove it from all subscribed topics")
    void shouldCascadeUnregisterToSubscriptions() {
        // given
        final WsSession session1 = createMockSession("session-1");
        final WsSession session2 = createMockSession("session-2");
        final WsTopic topicGlobal = createTopic("global");
        final WsTopic topicPrivate = createTopic("private-room");
        registry.register(session1);
        registry.register(session2);
        registry.subscribe(session1, topicGlobal);
        registry.subscribe(session1, topicPrivate);
        registry.subscribe(session2, topicGlobal);
        assertThat(registry.getSubscribers(topicGlobal)).hasSize(2);
        assertThat(registry.getSubscribers(topicPrivate)).hasSize(1);
        // when
        registry.unregister(session1);
        // then
        assertThat(registry.getAllSessions())
            .as("Only session2 should remain active")
            .containsExactly(session2);
        assertThat(registry.getSubscribers(topicGlobal))
            .as("Global topic should only contain session2")
            .containsExactly(session2);
        assertThat(registry.getSubscribers(topicPrivate))
            .as("Private topic should be empty and cleaned up")
            .isEmpty();
    }

    @Test
    @DisplayName("should return empty collection for unknown topics instead of null")
    void shouldReturnEmptyForUnknownTopics() {
        // given
        final WsTopic ghostTopic = createTopic("nobody-cares");
        // when
        final Collection<WsSession> subscribers = registry.getSubscribers(ghostTopic);
        final Collection<WsSession> unsafeSubscribers = registry
            .getUnsafeSubscribers("another-ghost");
        // then
        assertThat(subscribers).isNotNull().isEmpty();
        assertThat(unsafeSubscribers).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("should ignore multiple unsubscriptions from the same topic")
    void shouldIgnoreRedundantUnsubscribes() {
        // given
        final WsSession session = createMockSession("session-x");
        final WsTopic topic = createTopic("alerts");
        registry.register(session);
        registry.subscribe(session, topic);
        // when
        registry.unsubscribe(session, topic);
        registry.unsubscribe(session, topic); // redundancy check
        registry.unsubscribe(session, createTopic("never-subscribed"));
        // then
        assertThat(registry.getSubscribers(topic)).isEmpty();
    }

    @Test
    @DisplayName("should allow one session to safely subscribe to the same topic multiple times")
    void shouldHandleRedundantSubscribes() {
        // given
        final WsSession session = createMockSession("session-redundant");
        final WsTopic topic = createTopic("redundant-alerts");
        registry.register(session);
        // when
        registry.subscribe(session, topic);
        registry.subscribe(session, topic);
        // then
        assertThat(registry.getSubscribers(topic))
            .as("Set semantics should prevent duplicates")
            .hasSize(1)
            .containsExactly(session);
    }

    private WsSession createMockSession(String id) {
        WsSession session = mock(WsSession.class);
        lenient().when(session.getSessionId()).thenReturn(id);
        return session;
    }

    private WsTopic createTopic(String name) {
        return () -> name;
    }
}
