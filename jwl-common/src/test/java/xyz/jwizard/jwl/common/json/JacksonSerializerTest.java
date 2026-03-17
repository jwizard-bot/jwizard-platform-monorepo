package xyz.jwizard.jwl.common.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JacksonSerializerTest {
    private final JacksonSerializer serializer = new JacksonSerializer();

    @Test
    @DisplayName("should throw clean exception message on malformed JSON")
    void shouldThrowCleanException() {
        // missing comma
        final String badJson = "{ \"name\": JWizard }";
        final ByteArrayInputStream in = new ByteArrayInputStream(badJson.getBytes());

        assertThatThrownBy(() -> serializer.deserialize(in, Simple.class))
            .isInstanceOf(JsonException.class);
    }

    record Simple(String name) {
    }
}
