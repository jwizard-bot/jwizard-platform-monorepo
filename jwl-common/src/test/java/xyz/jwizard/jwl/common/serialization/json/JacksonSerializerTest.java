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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;
import xyz.jwizard.jwl.common.serialization.envelope.MessageEnvelope;
import xyz.jwizard.jwl.common.serialization.envelope.OpCode;
import xyz.jwizard.jwl.common.serialization.envelope.TestOpCode;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class JacksonSerializerTest {
    private final JacksonSerializer serializer = JacksonSerializer.createDefaultStrictMapper();

    @Test
    @DisplayName("should throw clean exception message on malformed JSON")
    void shouldThrowCleanException() {
        // given, missing comma
        final String badJson = "{ \"name\": JWizard }";
        final ByteArrayInputStream in = new ByteArrayInputStream(badJson.getBytes());
        // when & then
        assertThatThrownBy(() -> serializer.deserialize(in, Simple.class))
            .isInstanceOf(JsonSerializerException.class);
    }

    @Test
    @DisplayName("should return correct format")
    void shouldReturnRawFormat() {
        assertThat(serializer.format()).isEqualTo(SerializerFormat.JSON);
    }

    @Test
    @DisplayName("should serialize and deserialize envelope using OpCode interface")
    void shouldHandleEnvelopeWithOpCode() {
        // given
        final OpCode op = TestOpCode.USER_DATA;
        final UserPayload payload = new UserPayload("JWizard", 2026);
        // when
        final byte[] serialized = serializer.serializeEnvelope(op, payload);
        final MessageEnvelope<?> deserialized = serializer.deserializeEnvelope(serialized, code -> {
            if (code == TestOpCode.USER_DATA.getCode()) {
                return UserPayload.class;
            }
            return null;
        });
        // then
        assertThat(deserialized.op()).isEqualTo(100);
        assertThat(deserialized.data()).isInstanceOf(UserPayload.class);
        final UserPayload resultData = (UserPayload) deserialized.data();
        assertThat(resultData.username()).isEqualTo("JWizard");
    }

    @Test
    @DisplayName("should correctly resolve types based on OpCode integer value")
    void shouldResolveTypesByIntCode() {
        // given
        final TestOpCode op = TestOpCode.HEARTBEAT;
        final String jsonString = String.format("{\"op\": %d, \"data\": {}}", op.getCode());
        final byte[] json = jsonString.getBytes();
        // when
        final MessageEnvelope<?> envelope = serializer.deserializeEnvelope(json, code -> {
            if (code == 200) {
                return Heartbeat.class;
            }
            return null;
        });
        // then
        assertThat(envelope.op()).isEqualTo(200);
        assertThat(envelope.data()).isInstanceOf(Heartbeat.class);
    }

    @Test
    @DisplayName("should throw exception for unknown op code not present in resolver")
    void shouldThrowOnUnknownCode() {
        // given
        final int unknownCode = 999_999;
        final String jsonString = String.format("{\"op\": %d, \"data\": {}}", unknownCode);
        // when & then
        assertThatThrownBy(() -> serializer
            .deserializeEnvelope(jsonString.getBytes(), code -> null))
            .isInstanceOf(MessageSerializerException.class)
            .hasMessageContaining("Unknown op code: 999");
    }

    @Test
    @DisplayName("should handle empty data when dataType is Void.class")
    void shouldHandleVoidDataType() {
        // given
        final TestOpCode op = TestOpCode.HEARTBEAT;
        final String jsonString = String.format("{\"op\": %d, \"data\": null}", op.getCode());
        // when
        final MessageEnvelope<?> envelope = serializer
            .deserializeEnvelope(jsonString.getBytes(), code -> Void.class);
        // then
        assertThat(envelope.data()).isNull();
    }

    @Test
    @DisplayName("should serialize only 'op' and 'data' fields, ignoring helper methods")
    void shouldSerializeOnlyRequiredFields() {
        // given
        final int opCode = (0x01 << 16) | 0x64;
        final UserPayload payload = new UserPayload("JWizard", 2026);
        final MessageEnvelope<UserPayload> envelope = new MessageEnvelope<>(opCode, payload);
        // when
        final String json = serializer.serialize(envelope);
        // then
        try {
            final JsonNode tree = new JsonMapper().readTree(json);
            final Collection<String> keys = tree.propertyNames();
            assertThat(keys)
                .as("Generated JSON should contain exactly 'op' and 'data' fields")
                .containsExactlyInAnyOrder("op", "data");
            assertThat(keys).doesNotContain("category", "actionId");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to verify JSON structure", ex);
        }
    }
}

record Simple(String name) {
}

record UserPayload(String username, int year) {
}

record Heartbeat() {
}
