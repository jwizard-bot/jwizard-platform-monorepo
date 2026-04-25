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
package xyz.jwizard.jwl.codec.envelope;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OpCodeTest {
    @Test
    @DisplayName("should correctly extract category from op code in envelope")
    void shouldExtractCategory() {
        // given: category 0x0A (10), action 0x05
        final int opCode = (0x0A << 16) | 0x05;
        final MessageEnvelope<String> envelope = new MessageEnvelope<>(opCode, "test");
        // then
        assertThat(envelope.category()).isEqualTo(0x0A);
    }

    @Test
    @DisplayName("should correctly extract action id from op code in envelope")
    void shouldExtractActionId() {
        // given: category 0xFF, action 0x7F (127)
        final int opCode = (0xFF << 16) | 0x7F;
        final MessageEnvelope<Void> envelope = new MessageEnvelope<>(opCode, null);
        // then
        assertThat(envelope.actionId()).isEqualTo(0x7F);
    }

    @Test
    @DisplayName("should verify category membership correctly in envelope")
    void shouldVerifyCategory() {
        // given
        final int opCode = (0x01 << 16) | 0x64;
        final MessageEnvelope<Object> envelope = new MessageEnvelope<>(opCode, new Object());
        // then
        assertThat(envelope.isCategory(0x01)).isTrue();
        assertThat(envelope.isCategory(0x02)).isFalse();
    }

    @Test
    @DisplayName("should correctly handle various op codes from TestOpCode enum")
    void shouldHandleEnumOpCodes() {
        // given
        final TestOpCode op = TestOpCode.USER_DATA;
        final MessageEnvelope<String> envelope = new MessageEnvelope<>(op.getCode(), "payload");
        // then
        assertThat(envelope.op()).isEqualTo(op.getCode());
        assertThat(envelope.category()).isEqualTo(0x01);
        assertThat(envelope.actionId()).isEqualTo(0x64);
    }
}
