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
package xyz.jwizard.jwl.websocket.listener;

import org.jspecify.annotations.Nullable;

import xyz.jwizard.jwl.common.util.CastUtil;
import xyz.jwizard.jwl.websocket.WsSession;
import xyz.jwizard.jwl.websocket.listener.action.pool.WsActionPool;

public abstract class WsMessageListener {
    protected final WsActionPool pool;

    protected WsMessageListener(AbstractBuilder<?> builder) {
        pool = builder.pool;
    }

    protected WsMessageListener(WsActionPool pool) {
        this.pool = pool;
    }

    public WsActionPool getPool() {
        return pool;
    }

    public abstract void onMessage(WsSession session, byte[] message);

    public abstract void onMessage(WsSession session, String message);

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>> {
        private WsActionPool pool = null;

        protected AbstractBuilder() {
        }

        protected B self() {
            return CastUtil.unsafeCast(this);
        }

        public B pool(@Nullable WsActionPool pool) {
            this.pool = pool;
            return self();
        }

        public abstract WsMessageListener build();
    }
}
