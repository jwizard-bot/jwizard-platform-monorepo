package xyz.jwizard.jwl.common.serialization.json;

import java.io.InputStream;

public interface JsonSerializer {
    // raw <-> data
    String serialize(Object value) throws JsonSerializerException;

    // data <-> raw
    <T> T deserialize(InputStream input, Class<T> type) throws JsonSerializerException;
}
