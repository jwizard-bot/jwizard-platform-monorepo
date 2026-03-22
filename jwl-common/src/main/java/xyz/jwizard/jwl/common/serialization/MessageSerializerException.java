package xyz.jwizard.jwl.common.serialization;

public class MessageSerializerException extends RuntimeException {
    public MessageSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSerializerException(String message) {
        super(message);
    }
}
