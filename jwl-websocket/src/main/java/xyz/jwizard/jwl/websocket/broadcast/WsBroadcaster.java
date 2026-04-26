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
package xyz.jwizard.jwl.websocket.broadcast;

import xyz.jwizard.jwl.codec.envelope.OpCode;

import java.nio.charset.StandardCharsets;

public interface WsBroadcaster {
    void broadcast(OpCode op, Object payload);

    void broadcast(WsTopic topic, OpCode op, Object payload);

    default void broadcast(OpCode op) {
        broadcast(op, null);
    }

    default void broadcast(WsTopic topic, OpCode op) {
        broadcast(topic, op, null);
    }

    void broadcastRaw(byte[] payload);

    void broadcastRaw(WsTopic topic, byte[] payload);

    default void broadcastRaw(String payload) {
        broadcastRaw(payload.getBytes(StandardCharsets.UTF_8));
    }

    default void broadcastRaw(WsTopic topic, String payload) {
        broadcastRaw(topic, payload.getBytes(StandardCharsets.UTF_8));
    }
}
