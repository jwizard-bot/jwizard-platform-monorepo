package xyz.jwizard.jwl.common.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import xyz.jwizard.jwl.common.reflect.ClassScanner;

public class ApplicationContext {
    private final ComponentProvider componentProvider;

    public ApplicationContext(ClassScanner scanner) {
        final Injector injector = Guice.createInjector(new AutoScanModule(scanner));
        this.componentProvider = injector.getInstance(ComponentProvider.class);
    }

    public ComponentProvider getComponentProvider() {
        return componentProvider;
    }
}
