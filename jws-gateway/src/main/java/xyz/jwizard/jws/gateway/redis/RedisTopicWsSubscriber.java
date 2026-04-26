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
package xyz.jwizard.jws.gateway.redis;

import xyz.jwizard.jwl.kv.pubsub.KvChannel;
import xyz.jwizard.jwl.kv.pubsub.subscriber.AbstractKvSubscriber;
import xyz.jwizard.jwl.kv.pubsub.subscriber.SubscriptionMode;
import xyz.jwizard.jwl.websocket.dispatcher.LocalSessionDispatcher;
import xyz.jwizard.jws.gateway.WsKvChannel;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class RedisTopicWsSubscriber extends AbstractKvSubscriber<byte[]> {
    private final LocalSessionDispatcher localSessionDispatcher;

    @Inject
    RedisTopicWsSubscriber(LocalSessionDispatcher localSessionDispatcher) {
        this.localSessionDispatcher = localSessionDispatcher;
    }

    @Override
    public KvChannel getChannel() {
        return WsKvChannel.TOPIC_RECEIVE_EVENTS;
    }

    @Override
    public Class<byte[]> getPayloadType() {
        return byte[].class;
    }

    @Override
    public SubscriptionMode getMode() {
        return SubscriptionMode.PATTERN;
    }

    @Override
    public void handle(String channel, String[] params, byte[] message) {
        if (params != null && params.length > 0) {
            localSessionDispatcher.dispatchRaw(params[0], message);
        }
    }
}
