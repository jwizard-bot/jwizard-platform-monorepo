package xyz.jwizard.jwl.common.util;

import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;

public class Assert {
    private Assert() {
    }

    public static <T> T notNull(T object, String message) {
        state(object != null, message);
        return object;
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new CriticalBootstrapException(message);
        }
    }
}
