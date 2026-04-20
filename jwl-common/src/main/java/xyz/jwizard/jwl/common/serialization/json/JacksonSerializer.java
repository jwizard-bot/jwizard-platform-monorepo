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
package xyz.jwizard.jwl.common.serialization.json;

import java.io.InputStream;
import java.util.function.Function;

import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;
import xyz.jwizard.jwl.common.serialization.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.common.serialization.envelope.MessageEnvelope;
import xyz.jwizard.jwl.common.serialization.envelope.OpCode;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class JacksonSerializer implements JsonSerializer, EnvelopeSerializer {
    private final ObjectMapper objectMapper;

    private JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static JacksonSerializer createDefaultStrictMapper() {
        final ObjectMapper mapper = JsonMapper.builder()
            // error when a field required by the constructor/record is missing in the JSON
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            // error when the JSON contains properties that do not exist in our class
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // prevents setting null for primitive types (int, boolean, etc.)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
        return new JacksonSerializer(mapper);
    }

    // for loosely coupled service as queues (RabbitMQ, Kafka)
    public static JacksonSerializer createLenientForMessaging() {
        final ObjectMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
        return new JacksonSerializer(mapper);
    }

    @Override
    public String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException ex) {
            throw new JsonSerializerException(getCleanMessage(ex), ex);
        }
    }

    @Override
    public <T> T deserialize(InputStream input, Class<T> type) {
        try {
            return objectMapper.readValue(input, type);
        } catch (JacksonException ex) {
            throw new JsonSerializerException(getCleanMessage(ex), ex);
        }
    }

    @Override
    public byte[] serializeToBytes(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JacksonException ex) {
            throw new MessageSerializerException(getCleanMessage(ex), ex);
        }
    }

    @Override
    public <T> T deserializeFromBytes(byte[] bytes, Class<T> type) {
        try {
            return objectMapper.readValue(bytes, type);
        } catch (JacksonException ex) {
            throw new MessageSerializerException(getCleanMessage(ex), ex);
        }
    }

    @Override
    public SerializerFormat format() {
        return SerializerFormat.JSON;
    }

    @Override
    public byte[] serializeEnvelope(OpCode opCode, Object payload) {
        final MessageEnvelope<Object> envelope = new MessageEnvelope<>(opCode.getCode(), payload);
        return serializeToBytes(envelope);
    }

    @Override
    public MessageEnvelope<?> deserializeEnvelope(byte[] payload,
                                                  Function<Integer, Class<?>> typeResolver) {
        try {
            final JsonNode tree = objectMapper.readTree(payload);
            final JsonNode opNode = tree.get("op");
            if (opNode == null) {
                throw new MessageSerializerException("Missing 'op' field in envelope");
            }
            final int op = opNode.intValue();
            final Class<?> dataType = typeResolver.apply(op);
            if (dataType == null) {
                throw new MessageSerializerException(String
                    .format("Unknown op code: 0x%08X (%d)", op, op));
            }
            final JsonNode dataNode = tree.get("data");
            Object data = null;
            if (dataNode != null && !dataNode.isNull() && dataType != Void.class) {
                data = objectMapper.treeToValue(dataNode, dataType);
            }
            return new MessageEnvelope<>(op, data);
        } catch (JacksonException ex) {
            throw new MessageSerializerException(getCleanMessage(ex), ex);
        }
    }

    private String getCleanMessage(JacksonException ex) {
        return ex.getOriginalMessage().split(";")[0].trim();
    }
}
