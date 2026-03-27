package xyz.jwizard.jwl.common.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.function.Predicate;

public class IoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(IoUtil.class);

    private IoUtil() {
    }

    public static <T> void closeQuietly(T resource, CloseAction<T> closeAction) {
        if (resource == null || closeAction == null) {
            return;
        }
        try {
            closeAction.perform(resource);
        } catch (Exception ex) {
            LOG.warn("Failed to close resource safely", ex);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        closeQuietly(closeable, AutoCloseable::close);
    }

    public static void closeQuietly(Runnable runnable) {
        closeQuietly(runnable, Runnable::run);
    }

    public static <T> void closeQuietly(T resource, Predicate<T> predicate,
                                        CloseAction<T> closeAction) {
        closeQuietly(resource, r -> {
            if (predicate.test(r)) {
                closeAction.perform(r);
            }
        });
    }
}
