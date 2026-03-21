package xyz.jwizard.jwl.common.util.thread;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
