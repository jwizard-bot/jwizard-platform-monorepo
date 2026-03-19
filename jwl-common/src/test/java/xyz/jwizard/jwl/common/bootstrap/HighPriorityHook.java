package xyz.jwizard.jwl.common.bootstrap;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.di.ComponentProvider;

@Singleton
class HighPriorityHook implements TestHook {
    @Override
    public int priority() {
        return 100;
    }

    @Override
    public void onStart(ComponentProvider provider) {
    }
}
