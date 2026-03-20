package xyz.jwizard.jwl.common.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GracefulShutdownHook extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(GracefulShutdownHook.class);

    private final List<? extends LifecycleHook> hooks;
    private final CountDownLatch shutdownLatch;

    public GracefulShutdownHook(List<? extends LifecycleHook> hooks, CountDownLatch shutdownLatch) {
        this.hooks = hooks;
        this.shutdownLatch = shutdownLatch;
    }

    @Override
    public void run() {
        LOG.info("Shutting down application");
        final List<LifecycleHook> stopOrder = new ArrayList<>(hooks);
        Collections.reverse(stopOrder);
        stopOrder.forEach(LifecycleHook::onStop);
        shutdownLatch.countDown();
    }
}
