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

import xyz.jwizard.jwl.kv.pubsub.pattern.ChannelParamExtractor;
import xyz.jwizard.jwl.kv.pubsub.subscriber.KvSubscriber;

import redis.clients.jedis.JedisPubSub;

public class JedisPubSubAdapter extends JedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(JedisPubSubAdapter.class);

    private final KvSubscriber<String> kvSubscriber;
    private final ChannelParamExtractor paramExtractor;

    public JedisPubSubAdapter(KvSubscriber<String> kvSubscriber,
                              ChannelParamExtractor paramExtractor) {
        this.kvSubscriber = kvSubscriber;
        this.paramExtractor = paramExtractor;
    }

    @Override
    public void onMessage(String channel, String message) {
        LOG.debug("KV RECEIVED (pubSub, String) -> channel: '{}'", channel);
        if (kvSubscriber != null) {
            kvSubscriber.handle(channel, new String[0], message);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOG.debug("KV RECEIVED (pattern, byte[]) -> pattern: '{}', channel: '{}'", pattern,
            channel);
        if (kvSubscriber != null) {
            final String[] extractedParams = paramExtractor.extract(channel);
            kvSubscriber.handle(channel, extractedParams, message);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        if (kvSubscriber != null) {
            kvSubscriber.setSubscribed(true);
            LOG.debug("Successfully subscribed to channel/pattern: '{}' (total active: {})",
                channel, subscribedChannels);
        }
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOG.debug("Unsubscribed from channel/pattern: '{}'", channel);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        onSubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        onUnsubscribe(pattern, subscribedChannels);
    }
}
