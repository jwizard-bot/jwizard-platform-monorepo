package xyz.jwizard.jwl.common.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.di.ApplicationContext;
import xyz.jwizard.jwl.common.reflect.ClassGraphScanner;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.util.ArrayUtil;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class DefaultBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBootstrapper.class);

    private static final CountDownLatch SHUTDOWN_LATCH = new CountDownLatch(1);
    private static final String JWL_SUFFIX = ".jwl";

    private DefaultBootstrapper() {
    }

    public static void run(Class<?> primarySource) {
        final String[] packagesToScan = getPackagesToScan(primarySource);
        LOG.info("Start bootstrapping application on package(s): {}",
            Arrays.asList(packagesToScan));

        final long startTime = System.currentTimeMillis();
        try (final ClassScanner scanner = new ClassGraphScanner(packagesToScan)) {
            final ApplicationContext context = new ApplicationContext(scanner);

            final List<? extends LifecycleHook> hooks = discoverAndSortHooks(scanner, context);
            registerShutdownHook(hooks);
            startHooks(hooks, context);
            awaitTermination(startTime);

        } catch (CriticalBootstrapException ex) {
            LOG.error("FATAL ERROR DURING APPLICATION STARTUP: {}", ex.getMessage(), ex);
            System.exit(1);
        } catch (InterruptedException ex) {
            LOG.warn("Main thread interrupted, initiating shutdown");
            Thread.currentThread().interrupt(); // restore interrupt flag
        } catch (Exception ex) {
            LOG.error("Error during startup: ", ex);
        }
    }

    private static List<? extends LifecycleHook> discoverAndSortHooks(ClassScanner scanner,
                                                                      ApplicationContext context) {
        return scanner.getSubtypesOf(LifecycleHook.class).stream()
            .map(clazz -> context.getComponentProvider().getInstance(clazz))
            .sorted(Comparator.comparingInt(LifecycleHook::priority))
            .toList();
    }

    private static void registerShutdownHook(List<? extends LifecycleHook> hooks) {
        final GracefulShutdownHook shutdownThread = new GracefulShutdownHook(hooks, SHUTDOWN_LATCH);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    private static void startHooks(List<? extends LifecycleHook> hooks,
                                   ApplicationContext context) {
        for (final LifecycleHook hook : hooks) {
            final String name = hook.getClass().getSimpleName();
            try {
                LOG.debug("Starting lifecycle hook: {} (priority: {})", name, hook.priority());
                hook.onStart(context.getComponentProvider());
            } catch (Exception ex) {
                throw new CriticalBootstrapException("Failed to start hook: " + name, ex);
            }
        }
    }

    private static void awaitTermination(long startTime) throws InterruptedException {
        final long durationMs = System.currentTimeMillis() - startTime;
        LOG.info("Bootstrapped and started successfully in {}s",
            String.format("%.3f", durationMs / 1000.0));
        try {
            System.in.close();
        } catch (Exception e) {
            LOG.debug("Could not close System.in", e);
        }
        SHUTDOWN_LATCH.await();
    }

    private static String[] getPackagesToScan(Class<?> primarySource) {
        final Set<String> packagesToScan = new HashSet<>();
        packagesToScan.add(primarySource.getPackageName());
        String jwlRoot = DefaultBootstrapper.class.getPackageName();
        if (jwlRoot.contains(JWL_SUFFIX)) {
            jwlRoot = jwlRoot.substring(0, jwlRoot.indexOf(JWL_SUFFIX) + JWL_SUFFIX.length());
        }
        packagesToScan.add(jwlRoot);
        if (primarySource.isAnnotationPresent(AppBootstrapper.class)) {
            final AppBootstrapper appInitializer = primarySource
                .getAnnotation(AppBootstrapper.class);
            Collections.addAll(packagesToScan, appInitializer.scanPackages());
        }
        return ArrayUtil.toArray(packagesToScan, String.class);
    }
}
