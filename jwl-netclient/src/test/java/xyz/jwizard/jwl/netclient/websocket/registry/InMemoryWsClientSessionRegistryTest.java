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
package xyz.jwizard.jwl.netclient.websocket.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xyz.jwizard.jwl.net.ws.WsCloseCode;
import xyz.jwizard.jwl.netclient.group.ClientGroup;
import xyz.jwizard.jwl.netclient.websocket.WsClientSession;

class InMemoryWsClientSessionRegistryTest {
    @Test
    @DisplayName("should close old session when new one registers for same group")
    void shouldCloseOldSessionWhenNewOneRegistersForSameGroup() {
        // given
        final InMemoryWsClientSessionRegistry registry = InMemoryWsClientSessionRegistry
            .createDefault();
        ClientGroup group = mock(ClientGroup.class);
        when(group.getClientGroupName()).thenReturn("group-1");
        final WsClientSession oldSession = mock(WsClientSession.class);
        when(oldSession.getGroup()).thenReturn(group);
        when(oldSession.isClosed()).thenReturn(false);
        final WsClientSession newSession = mock(WsClientSession.class);
        when(newSession.getGroup()).thenReturn(group);
        registry.register(oldSession);
        // when
        registry.register(newSession);
        // then
        verify(oldSession).close(WsCloseCode.REPLACED_SESSION);
        assertThat(registry.getSessions(group)).containsExactly(newSession);
    }
}
