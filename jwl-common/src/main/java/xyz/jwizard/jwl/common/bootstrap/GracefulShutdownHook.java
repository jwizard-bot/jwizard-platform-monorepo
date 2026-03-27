package xyz.jwizard.jwl.common.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.util.io.IoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GracefulShutdownHook extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(GracefulShutdownHook.class);

    private final List<? extends LifecycleHook> hooks;
    private final CountDownLatch shutdownLatch;

    public GracefulShutdownHook(List<? extends LifecycleHook> hooks, CountDownLatch shutdownLatch) {
        super("shutdown-t");
        this.hooks = hooks;
        this.shutdownLatch = shutdownLatch;
    }

    @Override
    public void run() {
        LOG.info("Initiating graceful shutdown sequence");
        final List<LifecycleHook> stopOrder = new ArrayList<>(hooks);
        Collections.reverse(stopOrder);
        for (final LifecycleHook hook : stopOrder) {
            final String hookName = hook.getClass().getSimpleName();
            LOG.info("Stopping component: [{}]", hookName);
            IoUtil.closeQuietly(hook, LifecycleHook::onStop);
        }
        LOG.info("Graceful shutdown sequence completed.");
        shutdownLatch.countDown();
    }
}
