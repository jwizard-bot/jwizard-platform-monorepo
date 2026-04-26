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

import xyz.jwizard.jwl.kv.pubsub.PubSubBroadcaster;
import xyz.jwizard.jwl.websocket.broadcast.WsMessageSink;
import xyz.jwizard.jwl.websocket.broadcast.WsTopic;
import xyz.jwizard.jws.gateway.WsKvChannel;

public class RedisWsMessageSink implements WsMessageSink {
    private final PubSubBroadcaster pubSubBroadcaster;

    private RedisWsMessageSink(PubSubBroadcaster pubSubBroadcaster) {
        this.pubSubBroadcaster = pubSubBroadcaster;
    }

    public static RedisWsMessageSink createDefault(PubSubBroadcaster pubSubBroadcaster) {
        return new RedisWsMessageSink(pubSubBroadcaster);
    }

    @Override
    public void payload(WsTopic topic, byte[] payload) {
        pubSubBroadcaster.publishBinary(WsKvChannel.TOPIC_BROADCAST, payload, topic.getTopic());
    }

    @Override
    public void payloadAll(byte[] payload) {
        pubSubBroadcaster.publishBinary(WsKvChannel.GLOBAL_BROADCAST, payload);
    }
}
