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
package xyz.jwizard.jwl.netclient.websocket.action;

import xyz.jwizard.jwl.codec.envelope.OpCode;
import xyz.jwizard.jwl.net.envelope.EnvelopeAction;
import xyz.jwizard.jwl.netclient.websocket.TestQueueProvider;
import xyz.jwizard.jwl.netclient.websocket.TestWsOpCode;
import xyz.jwizard.jwl.netclient.websocket.WsClientSession;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ReceiveDataAction implements EnvelopeAction<WsClientSession, String> {
    private final TestQueueProvider testQueueProvider;

    @Inject
    public ReceiveDataAction(TestQueueProvider testQueueProvider) {
        this.testQueueProvider = testQueueProvider;
    }

    @Override
    public void handle(WsClientSession channel, String data) {
        testQueueProvider.get().add(data);
    }

    @Override
    public OpCode opCode() {
        return TestWsOpCode.RECEIVE_DATA;
    }

    @Override
    public Class<String> payloadClass() {
        return String.class;
    }
}
