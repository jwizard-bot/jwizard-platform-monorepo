package xyz.jwizard.jwl.common.serialization.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonSerializerTest {
    private final JacksonSerializer serializer = JacksonSerializer.createDefaultStrictMapper();

    @Test
    @DisplayName("should throw clean exception message on malformed JSON")
    void shouldThrowCleanException() {
        // missing comma
        final String badJson = "{ \"name\": JWizard }";
        final ByteArrayInputStream in = new ByteArrayInputStream(badJson.getBytes());

        assertThatThrownBy(() -> serializer.deserialize(in, Simple.class))
            .isInstanceOf(JsonSerializerException.class);
    }

    @Test
    @DisplayName("should return correct format")
    void shouldReturnRawFormat() {
        assertThat(serializer.format()).isEqualTo(SerializerFormat.JSON);
    }

    record Simple(String name) {
    }
}
