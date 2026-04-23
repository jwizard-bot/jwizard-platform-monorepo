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
package xyz.jwizard.jwl.common.serialization.raw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.StandardSerializerFormat;

class RawByteSerializerTest {
    private final RawByteSerializer serializer = RawByteSerializer.createDefault();

    @Test
    @DisplayName("should return the same byte array on serialization")
    void shouldSerializeRawBytes() {
        // given
        final byte[] input = "hello jwizard".getBytes();
        // when
        final byte[] result = serializer.serializeToBytes(input);
        // then
        assertThat(result).isSameAs(input);
    }

    @Test
    @DisplayName("should return empty array when serializing null")
    void shouldSerializeNullAsEmptyArray() {
        // when
        final byte[] result = serializer.serializeToBytes(null);
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should throw exception when trying to serialize non-byte-array object")
    void shouldThrowOnInvalidSerializationType() {
        // given
        final String invalidInput = "I am not a byte array";
        // then
        assertThatThrownBy(() -> serializer.serializeToBytes(invalidInput))
            .isInstanceOf(MessageSerializerException.class)
            .hasMessageContaining("RawByteSerializer can only handle byte[]");
    }

    @Test
    @DisplayName("should return the same byte array on deserialization to byte[].class")
    void shouldDeserializeRawBytes() {
        // given
        final byte[] input = {0x01, 0x02, 0x03};
        // when
        final byte[] result = serializer.deserializeFromBytes(input, byte[].class);
        // then
        assertThat(result).isSameAs(input);
    }

    @Test
    @DisplayName("should throw exception when requesting deserialization to type other than byte[]")
    void shouldThrowOnInvalidDeserializationType() {
        // given
        final byte[] input = new byte[0];
        // then
        assertThatThrownBy(() -> serializer.deserializeFromBytes(input, String.class))
            .isInstanceOf(MessageSerializerException.class)
            .hasMessageContaining("RawByteSerializer can only deserialize to byte[].class");
    }

    @Test
    @DisplayName("should return correct format")
    void shouldReturnRawFormat() {
        assertThat(serializer.format()).isEqualTo(StandardSerializerFormat.RAW);
    }
}
