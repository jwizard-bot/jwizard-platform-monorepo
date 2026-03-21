package xyz.jwizard.jwl.common.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadUtil.class);

    private ThreadUtil() {
    }

    public static void runAsync(String name, ThrowingRunnable task) {
        Thread.ofVirtual()
            .name(name)
            .start(() -> {
                try {
                    task.run();
                } catch (Exception ex) {
                    LOG.error("Critical error in async task: {}", name, ex);
                }
            });
    }
}
