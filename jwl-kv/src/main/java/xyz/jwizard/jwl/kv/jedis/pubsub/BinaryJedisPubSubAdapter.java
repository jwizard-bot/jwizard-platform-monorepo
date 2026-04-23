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

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.kv.pubsub.pattern.ChannelParamExtractor;
import xyz.jwizard.jwl.kv.pubsub.subscriber.KvSubscriber;

import redis.clients.jedis.BinaryJedisPubSub;

public class BinaryJedisPubSubAdapter extends BinaryJedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryJedisPubSubAdapter.class);

    private final KvSubscriber<byte[]> kvSubscriber;
    private final ChannelParamExtractor paramExtractor;

    public BinaryJedisPubSubAdapter(KvSubscriber<byte[]> kvSubscriber,
                                    ChannelParamExtractor paramExtractor) {
        this.kvSubscriber = kvSubscriber;
        this.paramExtractor = paramExtractor;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        final String channelStr = new String(channel, StandardCharsets.UTF_8);
        if (LOG.isDebugEnabled()) {
            LOG.debug("KV RECEIVED (pubSub, byte[]) -> channel: '{}'", channelStr);
        }
        if (kvSubscriber != null) {
            kvSubscriber.handle(channelStr, new String[0], message);
        }
    }

    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
        final String channelStr = new String(channel, StandardCharsets.UTF_8);
        if (LOG.isDebugEnabled()) {
            final String patternStr = new String(pattern, StandardCharsets.UTF_8);
            LOG.debug("KV RECEIVED (pattern, byte[]) -> pattern: '{}', channel: '{}'", patternStr,
                channelStr);
        }
        if (kvSubscriber != null) {
            final String[] extractedParams = paramExtractor.extract(channelStr);
            kvSubscriber.handle(channelStr, extractedParams, message);
        }
    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        if (kvSubscriber != null) {
            kvSubscriber.setSubscribed(true);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Successfully subscribed to channel/pattern: '{}' (total active: {})",
                    new String(channel, StandardCharsets.UTF_8), subscribedChannels);
            }
        }
    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        LOG.debug("Unsubscribed from channel/pattern: '{}'", channel);
    }

    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {
        onSubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
        onUnsubscribe(pattern, subscribedChannels);
    }
}
