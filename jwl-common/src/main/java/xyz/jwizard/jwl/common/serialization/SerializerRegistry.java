package xyz.jwizard.jwl.common.serialization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerRegistry {
    private final Map<SerializerFormat, MessageSerializer> serializers = new ConcurrentHashMap<>();

    public SerializerRegistry register(MessageSerializer serializer) {
        serializers.put(serializer.format(), serializer);
        return this;
    }

    public MessageSerializer get(SerializerFormat format) {
        final MessageSerializer serializer = serializers.get(format);
        if (serializer == null) {
            throw new IllegalArgumentException("No registered handler for: " + format);
        }
        return serializer;
    }
}
