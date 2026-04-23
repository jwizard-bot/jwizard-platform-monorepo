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
package xyz.jwizard.jwl.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MessageSerializer extends Serializer {
    byte[] serializeToBytes(Object value);

    <T> T deserializeFromBytes(byte[] bytes, Class<T> type);

    default void serializeToStream(Object value, OutputStream out) throws IOException {
        out.write(serializeToBytes(value));
    }

    default <T> T deserializeFromStream(InputStream in, Class<T> type) throws IOException {
        return deserializeFromBytes(in.readAllBytes(), type);
    }
}
