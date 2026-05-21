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
import xyz.jwizard.jwl.netclient.websocket.TestWsOpCode;
import xyz.jwizard.jwl.netclient.websocket.protobuf.TestPayloadProto;
import xyz.jwizard.jwl.websocket.WsSession;

import jakarta.inject.Singleton;

@Singleton
public class SendDataProtobufAction
    implements EnvelopeAction<WsSession, TestPayloadProto.MyMessage> {
    @Override
    public void handle(WsSession channel, TestPayloadProto.MyMessage data) {
        final TestPayloadProto.MyMessage response = data.toBuilder()
            .setContent("Received: " + data.getContent())
            .build();
        channel.sendEnvelope(TestWsOpCode.RECEIVE_DATA_PROTO, response);
    }

    @Override
    public OpCode opCode() {
        return TestWsOpCode.SEND_DATA_PROTO;
    }

    @Override
    public Class<TestPayloadProto.MyMessage> payloadClass() {
        return TestPayloadProto.MyMessage.class;
    }
}
