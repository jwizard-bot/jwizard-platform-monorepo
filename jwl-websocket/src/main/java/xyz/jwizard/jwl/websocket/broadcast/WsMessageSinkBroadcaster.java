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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.OpCode;

public class WsMessageSinkBroadcaster implements WsBroadcaster {
    private static final Logger LOG = LoggerFactory.getLogger(WsMessageSinkBroadcaster.class);

    private final WsMessageSink sink;
    private final EnvelopeSerializer<?> serializer;

    public WsMessageSinkBroadcaster(WsMessageSink sink, EnvelopeSerializer<?> serializer) {
        this.sink = sink;
        this.serializer = serializer;
    }

    @Override
    public void broadcast(OpCode op, Object payload) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Broadcasting byte message globally, OP: {}", op);
        }
        sink.payloadAll(serializer.serializeEnvelopeAsBytes(op, payload));
    }

    @Override
    public void broadcast(WsTopic topic, OpCode op, Object payload) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Broadcasting byte message to topic: [{}], OP: {}", topic, op);
        }
        sink.payload(topic, serializer.serializeEnvelopeAsBytes(op, payload));
    }

    @Override
    public void broadcastRaw(byte[] payload) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Broadcasting RAW bytes globally (size: {} bytes)", payload.length);
        }
        sink.payloadAll(payload);
    }

    @Override
    public void broadcastRaw(WsTopic topic, byte[] payload) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Broadcasting RAW bytes to topic: [{}] (size: {} bytes)", topic,
                payload.length);
        }
        sink.payload(topic, payload);
    }
}
