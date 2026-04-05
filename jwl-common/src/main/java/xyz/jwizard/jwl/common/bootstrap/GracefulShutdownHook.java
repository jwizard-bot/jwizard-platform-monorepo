/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private final boolean wait;

    public GracefulShutdownHook(List<? extends LifecycleHook> hooks, CountDownLatch shutdownLatch,
                                boolean wait) {
        super("shutdown-t");
        this.hooks = hooks;
        this.shutdownLatch = shutdownLatch;
        this.wait = wait;
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
        LOG.info("Graceful shutdown sequence completed");
        if (wait) {
            shutdownLatch.countDown();
        }
    }
}
