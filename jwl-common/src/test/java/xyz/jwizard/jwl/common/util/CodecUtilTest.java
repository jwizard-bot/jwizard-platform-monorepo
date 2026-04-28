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
package xyz.jwizard.jwl.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CodecUtilTest {
    @Test
    @DisplayName("should encode string to Base64 format")
    void shouldEncodeBase64() {
        // given
        final String rawText = "jwl:secretPassword123!";
        final String expectedBase64 = "andsOnNlY3JldFBhc3N3b3JkMTIzIQ==";
        // when
        final String encoded = CodecUtil.encodeBase64(rawText);
        // then
        assertThat(encoded).isEqualTo(expectedBase64);
    }

    @Test
    @DisplayName("should decode Base64 string to raw text")
    void shouldDecodeBase64() {
        // given
        final String encodedBase64 = "andsOnNlY3JldFBhc3N3b3JkMTIzIQ==";
        final String expectedText = "jwl:secretPassword123!";
        // when
        final String decoded = CodecUtil.decodeBase64(encodedBase64);
        // then
        assertThat(decoded).isEqualTo(expectedText);
    }

    @Test
    @DisplayName("should encode and decode special UTF-8 characters correctly")
    void shouldHandleUtf8Characters() {
        // given
        final String rawText = "Zażółć gęślą jaźń";
        // when
        final String encoded = CodecUtil.encodeBase64(rawText);
        final String decoded = CodecUtil.decodeBase64(encoded);
        // then
        assertThat(encoded).isNotEqualTo(rawText);
        assertThat(decoded).isEqualTo(rawText);
    }

    @Test
    @DisplayName("should return null when input is null")
    void shouldReturnNullForNullInput() {
        // when & then
        assertThat(CodecUtil.encodeBase64(null)).isNull();
        assertThat(xyz.jwizard.jwl.common.util.CodecUtil.decodeBase64(null)).isNull();
    }

    @Test
    @DisplayName("should throw exception when decoding invalid Base64 string")
    void shouldThrowExceptionForInvalidBase64() {
        // given
        final String invalidBase64 = "This is not valid base64!@#";
        // when & then
        assertThatThrownBy(() -> xyz.jwizard.jwl.common.util.CodecUtil.decodeBase64(invalidBase64))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
