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

import java.util.function.Function;

import xyz.jwizard.jwl.codec.EncodedPayloadVisitor;
import xyz.jwizard.jwl.codec.UnifiedMessageCodec;
import xyz.jwizard.jwl.codec.UnsupportedDataTypeException;

public interface EnvelopeSerializer<T> extends UnifiedMessageCodec {
    T serializeForSession(OpCode opCode, Object payload);

    byte[] serializeEnvelopeAsBytes(OpCode opCode, Object payload);

    default String serializeEnvelopeAsString(OpCode opCode, Object payload) {
        throw new UnsupportedDataTypeException("Text frames are not supported by " + getFormat());
    }

    void acceptRaw(byte[] rawPayload, EncodedPayloadVisitor visitor);

    @Override
    default MessageEnvelope<?> unwrap(String payload, Function<Integer, Class<?>> typeResolver) {
        throw new UnsupportedDataTypeException("Text frames are not supported by " + getFormat());
    }
}
