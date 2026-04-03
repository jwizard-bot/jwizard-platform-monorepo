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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;

public class JedisPubSubAdapter extends JedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(JedisPubSubAdapter.class);

    private final Consumer<String> onMessageConsumer;

    public JedisPubSubAdapter(Consumer<String> onMessageConsumer) {
        this.onMessageConsumer = onMessageConsumer;
    }

    @Override
    public void onMessage(String channel, String message) {
        LOG.debug("KV RECEIVED (PubSub) -> channel: '{}'", channel);
        if (onMessageConsumer != null) {
            onMessageConsumer.accept(message);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOG.info("Successfully subscribed to channel: '{}' (total active: {})", channel,
            subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOG.info("Unsubscribed from channel: '{}'", channel);
    }
}
