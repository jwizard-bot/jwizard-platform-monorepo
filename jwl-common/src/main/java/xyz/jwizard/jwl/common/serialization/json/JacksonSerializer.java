package xyz.jwizard.jwl.common.serialization.json;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;

public class JacksonSerializer implements JsonSerializer {
    private final ObjectMapper objectMapper;

    private JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static JacksonSerializer createDefaultStrictMapper() {
        final ObjectMapper mapper = JsonMapper.builder()
            // error when a field required by the constructor/record is missing in the JSON
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            // error when the JSON contains properties that do not exist in our class
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // prevents setting null for primitive types (int, boolean, etc.)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
        return new JacksonSerializer(mapper);
    }

    @Override
    public String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException ex) {
            throw new JsonSerializerException(getCleanMessage(ex), ex);
        }
    }

    @Override
    public <T> T deserialize(InputStream input, Class<T> type) {
        try {
            return objectMapper.readValue(input, type);
        } catch (JacksonException ex) {
            throw new JsonSerializerException(getCleanMessage(ex), ex);
        }
    }

    private String getCleanMessage(JacksonException ex) {
        return ex.getOriginalMessage().split(";")[0].trim();
    }
}
