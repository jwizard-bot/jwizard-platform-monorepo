package xyz.jwizard.jwl.common.json;

import java.io.InputStream;

public interface JsonSerializer {
    // raw <-> data
    String serialize(Object value) throws JsonException;

    // data <-> raw
    <T> T deserialize(InputStream input, Class<T> type) throws JsonException;
}
