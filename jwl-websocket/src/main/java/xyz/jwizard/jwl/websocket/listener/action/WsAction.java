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
package xyz.jwizard.jwl.websocket.listener.action;

import org.jspecify.annotations.Nullable;

import xyz.jwizard.jwl.codec.envelope.OpCode;
import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.listener.action.pool.WsActionPool;

public interface WsAction<T> {
    void handle(WsSession session, T data);

    OpCode opCode();

    Class<T> payloadClass();

    // null means root pool handled by non-pooled action websocket handler
    @Nullable
    default WsActionPool pool() {
        return null;
    }
}
