package xyz.jwizard.jwl.common.serialization.json;

import xyz.jwizard.jwl.common.serialization.MessageSerializer;

import java.io.InputStream;

public interface JsonSerializer extends MessageSerializer {
    // raw <-> data
    String serialize(Object value) throws JsonSerializerException;

    // data <-> raw
    <T> T deserialize(InputStream input, Class<T> type) throws JsonSerializerException;
}
