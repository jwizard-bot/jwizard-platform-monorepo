package xyz.jwizard.jwl.common.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

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
}
