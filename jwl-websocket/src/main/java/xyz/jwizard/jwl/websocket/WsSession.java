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
package xyz.jwizard.jwl.websocket;

import java.util.function.Function;

import xyz.jwizard.jwl.codec.envelope.MessageEnvelope;
import xyz.jwizard.jwl.codec.envelope.OpCode;

public interface WsSession {
    String getSessionId();

    String getPrincipalId();

    void send(byte[] message);

    void send(String message);

    void sendEnvelope(OpCode opCode, Object data);

    default void sendEnvelope(OpCode opCode) {
        sendEnvelope(opCode, null);
    }

    void sendAdapted(byte[] payload);

    MessageEnvelope<?> unwrap(byte[] payload, Function<Integer, Class<?>> typeResolver);

    MessageEnvelope<?> unwrap(String payload, Function<Integer, Class<?>> typeResolver);

    void close(int statusCode, String reason);

    boolean isClosed();
}
