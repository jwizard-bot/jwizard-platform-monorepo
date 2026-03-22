package xyz.jwizard.jwl.common.serialization;

public interface MessageSerializer {
    byte[] serializeToBytes(Object value) throws MessageSerializerException;

    <T> T deserializeFromBytes(byte[] bytes, Class<T> type) throws MessageSerializerException;

    SerializerFormat format();
}
