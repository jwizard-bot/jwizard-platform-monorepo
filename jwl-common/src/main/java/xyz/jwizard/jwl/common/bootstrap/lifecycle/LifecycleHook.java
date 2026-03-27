package xyz.jwizard.jwl.common.bootstrap.lifecycle;

import xyz.jwizard.jwl.common.di.ComponentProvider;

import java.util.Collections;
import java.util.List;

public interface LifecycleHook {
    // defines hook classes witch must be started before this hook
    default List<Class<? extends LifecycleHook>> dependsOn() {
        return Collections.emptyList();
    }

    void onStart(ComponentProvider componentProvider);

    default void onStop() {
    }
}
