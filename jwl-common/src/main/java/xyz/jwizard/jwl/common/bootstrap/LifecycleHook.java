package xyz.jwizard.jwl.common.bootstrap;

import xyz.jwizard.jwl.common.di.ComponentProvider;

public interface LifecycleHook {
    // higher priority hooks start first
    // default is 0, negative values start later, positive earlier
    default int priority() {
        return 0;
    }

    void onStart(ComponentProvider provider);

    default void onStop() {
    }
}
