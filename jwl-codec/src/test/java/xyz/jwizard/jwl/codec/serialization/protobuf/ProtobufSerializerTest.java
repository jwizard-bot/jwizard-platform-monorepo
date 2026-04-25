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
package xyz.jwizard.jwl.codec.serialization.protobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.protobuf.MessageLite;

import xyz.jwizard.jwl.codec.serialization.StandardSerializerFormat;
import xyz.jwizard.jwl.common.reflect.ClassScanner;

@ExtendWith(MockitoExtension.class)
class ProtobufSerializerTest {
    @Mock
    private ClassScanner scanner;

    private ProtobufSerializer serializer;

    @BeforeEach
    void setUp() {
        // given
        when(scanner.getSubtypesOf(com.google.protobuf.MessageLite.class))
            .thenReturn(Set.of(
                TestMessage.class,
                ComplexMessage.class
            ));
        // when
        serializer = ProtobufSerializer.createDefault(scanner);
    }

    @Test
    @DisplayName("should register test message and handle round-trip serialization")
    void shouldHandleSerialization() {
        // given
        final TestMessage message = TestMessage.newBuilder()
            .setId(100)
            .setValue("test-content")
            .build();
        // when
        final byte[] bytes = serializer.serializeToBytes(message);
        final TestMessage result = serializer.deserializeFromBytes(bytes, TestMessage.class);
        // then
        assertThat(result.getId()).isEqualTo(100);
        assertThat(result.getValue()).isEqualTo("test-content");
    }

    @Test
    @DisplayName("should handle stream deserialization for test message")
    void shouldHandleStream() {
        // given
        final TestMessage message = TestMessage.newBuilder()
            .setId(1)
            .build();
        final ByteArrayInputStream in = new ByteArrayInputStream(message.toByteArray());
        // when
        final TestMessage result = serializer.deserializeFromStream(in, TestMessage.class);
        // then
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("should fail when type was not part of the initial scan")
    void shouldFailOnUnregisteredType() {
        // given
        final byte[] data = new byte[0];
        // when & then
        assertThatThrownBy(() -> serializer.deserializeFromBytes(data, OtherTestMessage.class))
            .isInstanceOf(ProtobufSerializerException.class)
            .hasMessageContaining("Type not registered");
    }

    @Test
    @DisplayName("should return correct format for protobuf")
    void shouldReturnFormat() {
        // when
        final var format = serializer.format();
        // then
        assertThat(format).isEqualTo(StandardSerializerFormat.PROTOBUF);
    }

    @Test
    @DisplayName("should handle composition where one proto class uses another")
    void shouldHandleCrossProtoComposition() {
        // given
        final TestMessage inner = TestMessage.newBuilder()
            .setId(99)
            .setValue("nested-content")
            .build();
        final ComplexMessage complex = ComplexMessage.newBuilder()
            .setDescription("root-container")
            .setCoreData(inner)
            .build();
        // when
        final byte[] bytes = serializer.serializeToBytes(complex);
        final ComplexMessage result = serializer.deserializeFromBytes(bytes, ComplexMessage.class);
        // then
        assertThat(result.getDescription()).isEqualTo("root-container");
        assertThat(result.getCoreData().getId()).isEqualTo(99);
        assertThat(result.getCoreData().getValue()).isEqualTo("nested-content");
    }
}

abstract class OtherTestMessage implements MessageLite {
}
