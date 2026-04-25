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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.codec.envelope.MessageEnvelope;
import xyz.jwizard.jwl.codec.envelope.TestOpCode;
import xyz.jwizard.jwl.codec.serialization.json.JsonSerializer;
import xyz.jwizard.jwl.codec.serialization.json.JsonSerializerException;

@ExtendWith(MockitoExtension.class)
class JsonEnvelopeDeserializationTest {
    @Mock
    private JsonSerializer jsonSerializerMock;
    private JsonTextEnvelopeSerializer serializer;
    private Function<Integer, Class<?>> typeResolver;

    @BeforeEach
    void setUp() {
        serializer = JsonTextEnvelopeSerializer.createDefault(jsonSerializerMock);
        typeResolver = op -> op == TestOpCode.USER_DATA.getCode() ? String.class : null;
    }

    @Test
    @DisplayName("should properly parse map into MessageEnvelope")
    void shouldParseValidEnvelope() {
        // given
        final Map<String, Object> mockTree = Map.of(
            "op", TestOpCode.USER_DATA.getCode(),
            "data", "test_payload"
        );
        when(jsonSerializerMock.deserialize(any(String.class), eq(Map.class))).thenReturn(mockTree);
        when(jsonSerializerMock.convert("test_payload", String.class)).thenReturn("test_payload");
        // when
        final MessageEnvelope<?> envelope = serializer.deserializeEnvelope("{}", typeResolver);
        // then
        assertThat(envelope.op()).isEqualTo(TestOpCode.USER_DATA.getCode());
        assertThat(envelope.data()).isEqualTo("test_payload");
    }

    @Test
    @DisplayName("should throw exception when 'op' field is missing")
    void shouldThrowWhenOpIsMissing() {
        // given
        final Map<String, Object> mockTree = Map.of("data", "no_op_here");
        when(jsonSerializerMock.deserialize(any(String.class), eq(Map.class))).thenReturn(mockTree);
        // then
        assertThatThrownBy(() -> serializer.deserializeEnvelope("{}", typeResolver))
            .isInstanceOf(JsonSerializerException.class)
            .hasMessageContaining("Missing or invalid 'op' field");
    }

    @Test
    @DisplayName("should throw exception when OP code is unknown to resolver")
    void shouldThrowOnUnknownOpCode() {
        // given
        final Map<String, Object> mockTree = Map.of("op", 999999);
        when(jsonSerializerMock.deserialize(any(String.class), eq(Map.class))).thenReturn(mockTree);
        // then
        assertThatThrownBy(() -> serializer.deserializeEnvelope("{}", typeResolver))
            .isInstanceOf(JsonSerializerException.class)
            .hasMessageContaining("Unknown OP code");
    }
}
