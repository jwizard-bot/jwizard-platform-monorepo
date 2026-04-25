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
package xyz.jwizard.jwl.codec.envelope.json;

import java.util.Map;
import java.util.function.Function;

import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.MessageEnvelope;
import xyz.jwizard.jwl.codec.envelope.OpCode;
import xyz.jwizard.jwl.codec.serialization.SerializerFormat;
import xyz.jwizard.jwl.codec.serialization.StandardSerializerFormat;
import xyz.jwizard.jwl.codec.serialization.json.JsonSerializer;
import xyz.jwizard.jwl.codec.serialization.json.JsonSerializerException;

public abstract class JsonEnvelopeSerializer<T> implements EnvelopeSerializer<T> {
    protected final JsonSerializer serializer;

    protected JsonEnvelopeSerializer(JsonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public SerializerFormat baseFormat() {
        return StandardSerializerFormat.JSON;
    }

    @Override
    public MessageEnvelope<?> deserializeEnvelope(byte[] payload,
                                                  Function<Integer, Class<?>> typeResolver) {
        final Map<?, ?> tree = serializer.deserializeFromBytes(payload, Map.class);
        return parseMapToEnvelope(tree, typeResolver);
    }

    @Override
    public MessageEnvelope<?> deserializeEnvelope(String payload,
                                                  Function<Integer, Class<?>> typeResolver) {
        final Map<?, ?> tree = serializer.deserialize(payload, Map.class);
        return parseMapToEnvelope(tree, typeResolver);
    }

    @Override
    public byte[] serializeEnvelopeAsBytes(OpCode opCode, Object payload) {
        final MessageEnvelope<Object> envelope = new MessageEnvelope<>(opCode.getCode(), payload);
        return serializer.serializeToBytes(envelope);
    }

    @Override
    public String serializeEnvelopeAsString(OpCode opCode, Object payload) {
        final MessageEnvelope<Object> envelope = new MessageEnvelope<>(opCode.getCode(), payload);
        return serializer.serialize(envelope);
    }

    private MessageEnvelope<?> parseMapToEnvelope(Map<?, ?> tree,
                                                  Function<Integer, Class<?>> typeResolver) {
        if (tree == null) {
            throw new JsonSerializerException("Received empty or null payload");
        }
        final Object opRaw = tree.get("op");
        if (!(opRaw instanceof Number opNumber)) {
            throw new JsonSerializerException("Missing or invalid 'op' field in envelope");
        }
        final int op = opNumber.intValue();
        final Class<?> dataType = typeResolver.apply(op);
        if (dataType == null) {
            return new MessageEnvelope<>(op, null); // checked in ActionRouterWsMessageListener
        }
        final Object rawData = tree.get("data");
        Object data = null;
        if (rawData != null && dataType != Void.class) {
            data = serializer.convert(rawData, dataType);
        }
        return new MessageEnvelope<>(op, data);
    }
}
