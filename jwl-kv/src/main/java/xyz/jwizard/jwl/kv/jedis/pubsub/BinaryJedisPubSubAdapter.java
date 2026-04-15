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
package xyz.jwizard.jwl.kv.jedis.pubsub;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.BinaryJedisPubSub;

public class BinaryJedisPubSubAdapter extends BinaryJedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryJedisPubSubAdapter.class);

    private final Consumer<byte[]> onMessageConsumer;

    public BinaryJedisPubSubAdapter(Consumer<byte[]> onMessageConsumer) {
        this.onMessageConsumer = onMessageConsumer;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        LOG.debug("KV RECEIVED (PubSub) -> channel: '{}'", channel);
        if (onMessageConsumer != null) {
            onMessageConsumer.accept(message);
        }
    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        LOG.info("Successfully subscribed to channel: '{}' (total active: {})", channel,
            subscribedChannels);
    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        LOG.info("Unsubscribed from channel: '{}'", channel);
    }
}
