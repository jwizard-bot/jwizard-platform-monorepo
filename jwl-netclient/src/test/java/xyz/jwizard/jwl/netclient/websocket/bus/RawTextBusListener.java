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
package xyz.jwizard.jwl.netclient.websocket.bus;

import xyz.jwizard.jwl.net.message.bus.TypedMessageBusListener;
import xyz.jwizard.jwl.netclient.websocket.TestQueueProvider;
import xyz.jwizard.jwl.netclient.websocket.WsClientSession;

public class RawTextBusListener extends TypedMessageBusListener<String, WsClientSession> {
    private final TestQueueProvider testQueueProvider;

    public RawTextBusListener(TestQueueProvider testQueueProvider) {
        this.testQueueProvider = testQueueProvider;
    }

    @Override
    protected void handle(WsClientSession session, String message) {
        testQueueProvider.get().add(message);
    }

    @Override
    protected Class<String> getTargetType() {
        return String.class;
    }
}
