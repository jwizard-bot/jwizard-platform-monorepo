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
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringUtilTest {
    @Test
    @DisplayName("should handle basic string truncation (null, empty, ASCII, shorter than max)")
    void shouldHandleBasicTruncation() {
        assertAll(
            () -> assertThat(StringUtil.truncateToUtf8Bytes(null, 10)).isNull(),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("", 10)).isEmpty(),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("Hello", 10)).isEqualTo("Hello"),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("Hello World", 5)).isEqualTo("Hello")
        );
    }

    @Test
    @DisplayName("should safely truncate complex multi-byte UTF-8 characters without corruption")
    void shouldSafelyTruncateMultibyteCharacters() {
        assertAll(
            () -> assertThat(StringUtil.truncateToUtf8Bytes("aą", 2)).isEqualTo("a"),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("aą", 3)).isEqualTo("aą"),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("Hi 🚀", 5)).isEqualTo("Hi "),
            () -> assertThat(StringUtil.truncateToUtf8Bytes("Hi 🚀!", 7)).isEqualTo("Hi 🚀")
        );
    }
}
