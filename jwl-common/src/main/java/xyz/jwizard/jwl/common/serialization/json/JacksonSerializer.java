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

import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class JacksonSerializer implements JsonSerializer {
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

    private String getCleanMessage(JacksonException ex) {
        return ex.getOriginalMessage().split(";")[0].trim();
    }
}
