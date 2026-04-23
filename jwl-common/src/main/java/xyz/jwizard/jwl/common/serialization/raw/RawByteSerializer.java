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

import xyz.jwizard.jwl.common.serialization.MessageSerializer;
import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.StandardSerializerFormat;
import xyz.jwizard.jwl.common.util.CastUtil;

public class RawByteSerializer implements MessageSerializer {
    private RawByteSerializer() {
    }

    public static RawByteSerializer createDefault() {
        return new RawByteSerializer();
    }

    @Override
    public byte[] serializeToBytes(Object value) {
        if (value == null) {
            return new byte[0];
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new MessageSerializerException(
            "RawByteSerializer can only handle byte[], but received: " + value.getClass().getName()
        );
    }

    @Override
    public <T> T deserializeFromBytes(byte[] bytes, Class<T> type) {
        if (type.isAssignableFrom(byte[].class)) {
            return CastUtil.unsafeCast(bytes);
        }
        throw new MessageSerializerException(
            "RawByteSerializer can only deserialize to byte[].class, but requested: " +
                type.getName()
        );
    }

    @Override
    public StandardSerializerFormat format() {
        return StandardSerializerFormat.RAW;
    }
}
