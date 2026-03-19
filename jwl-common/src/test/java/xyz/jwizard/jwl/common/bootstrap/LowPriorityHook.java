package xyz.jwizard.jwl.common.bootstrap;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.di.ComponentProvider;

@Singleton
class LowPriorityHook implements TestHook {
    @Override
    public int priority() {
        return 1;
    }

    @Override
    public void onStart(ComponentProvider provider) {
    }
}
