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
package xyz.jwizard.jwl.codec.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.codec.DataType;
import xyz.jwizard.jwl.codec.EncodedPayloadVisitor;

@ExtendWith(MockitoExtension.class)
class JsonTextSerializerTest {
    @Mock
    private JsonSerializer engineMock;

    @Mock
    private EncodedPayloadVisitor visitorMock;

    @Test
    @DisplayName("should delegate text serialization and call visitor")
    void shouldDelegateTextOperations() {
        // given
        final JsonTextSerializer serializer = JsonTextSerializer.create(engineMock);
        final Object dummyPayload = new Object();
        final String expectedString = "{\"key\":\"value\"}";
        given(engineMock.serialize(dummyPayload)).willReturn(expectedString);
        given(engineMock.deserialize(expectedString, String.class)).willReturn("parsed");
        // when
        final String parsed = serializer.deserializePayload(expectedString, String.class);
        // when
        serializer.serializeAndAccept(dummyPayload, visitorMock);
        // then
        assertThat(parsed).isEqualTo("parsed");
        assertThat(serializer.getCodecDataType()).isEqualTo(DataType.TEXT);
        verify(visitorMock).accept(expectedString);
    }
}
