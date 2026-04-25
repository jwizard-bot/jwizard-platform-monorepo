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
package xyz.jwizard.jwl.codec.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SerializerRegistryTest {
    private SerializerRegistry<Serializer> registry;

    @Mock
    private Serializer jsonSerializer;

    @Mock
    private Serializer protobufSerializer;

    @BeforeEach
    void setUp() {
        registry = SerializerRegistry.create();
    }

    @Test
    @DisplayName("should register and retrieve serializer by format")
    void shouldRegisterAndGetSerializer() {
        // given
        when(jsonSerializer.format()).thenReturn(StandardSerializerFormat.JSON);
        when(protobufSerializer.format()).thenReturn(StandardSerializerFormat.PROTOBUF);
        // when
        registry.register(jsonSerializer);
        registry.register(protobufSerializer);
        // then
        assertThat(registry.get(StandardSerializerFormat.JSON)).isEqualTo(jsonSerializer);
        assertThat(registry.get(StandardSerializerFormat.PROTOBUF)).isEqualTo(protobufSerializer);
    }

    @Test
    @DisplayName("should throw exception when serializer is not found")
    void shouldThrowExceptionWhenNotFound() {
        // then
        assertThatThrownBy(() -> registry.get(StandardSerializerFormat.RAW))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no registered serializer for key: RAW");
    }

    @Test
    @DisplayName("should overwrite serializer when registering with same format")
    void shouldOverwriteOnDuplicateRegistration() {
        // given
        when(jsonSerializer.format()).thenReturn(StandardSerializerFormat.JSON);
        registry.register(jsonSerializer);
        final Serializer newJsonSerializer = mock(Serializer.class);
        when(newJsonSerializer.format()).thenReturn(StandardSerializerFormat.JSON);
        // when
        registry.register(newJsonSerializer);
        // then
        assertThat(registry.get(StandardSerializerFormat.JSON)).isEqualTo(newJsonSerializer);
    }

    @Test
    @DisplayName("should return all registered serializers")
    void shouldReturnAllSerializers() {
        // given
        when(jsonSerializer.format()).thenReturn(StandardSerializerFormat.JSON);
        when(protobufSerializer.format()).thenReturn(StandardSerializerFormat.PROTOBUF);
        registry.register(jsonSerializer);
        registry.register(protobufSerializer);
        // when
        final Collection<Serializer> all = registry.getSerializers();
        // then
        assertThat(all).hasSize(2).containsExactlyInAnyOrder(jsonSerializer, protobufSerializer);
    }

    @Test
    @DisplayName("should support fluent api for registration")
    void shouldSupportFluentApi() {
        // given
        when(jsonSerializer.format()).thenReturn(StandardSerializerFormat.JSON);
        // when
        final SerializerRegistry<Serializer> returnedRegistry = registry.register(jsonSerializer);
        // then
        assertThat(returnedRegistry).isSameAs(registry);
    }
}
