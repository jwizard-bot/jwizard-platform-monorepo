package xyz.jwizard.jwl.common.serialization.raw;

import xyz.jwizard.jwl.common.serialization.MessageSerializer;
import xyz.jwizard.jwl.common.serialization.MessageSerializerException;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;

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
            return (T) bytes;
        }
        throw new MessageSerializerException(
            "RawByteSerializer can only deserialize to byte[].class, but requested: " +
                type.getName()
        );
    }

    @Override
    public SerializerFormat format() {
        return SerializerFormat.RAW;
    }
}
