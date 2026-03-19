package xyz.jwizard.jwl.common.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class IoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(IoUtil.class);

    private IoUtil() {
    }

    public static <T> void actionQuietly(T resource, CloseAction<T> action) {
        if (resource == null || action == null) {
            return;
        }
        try {
            action.perform(resource);
        } catch (Exception ex) {
            LOG.warn("Failed to close resource safely", ex);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        actionQuietly(closeable, AutoCloseable::close);
    }
}
