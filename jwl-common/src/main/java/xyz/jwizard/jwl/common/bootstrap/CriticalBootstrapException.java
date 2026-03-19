package xyz.jwizard.jwl.common.bootstrap;

public class CriticalBootstrapException extends RuntimeException {
    public CriticalBootstrapException(String message) {
        super(message);
    }

    public CriticalBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}
