package xyz.jwizard.jwl.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.reflect.ClassScanner;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

class AutoScanModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(AutoScanModule.class);

    private final ClassScanner classScanner;

    AutoScanModule(ClassScanner classScanner) {
        this.classScanner = classScanner;
    }

    @Override
    protected void configure() {
        LOG.debug("Auto-configuring bindings for @Injectable components...");
        bind(ComponentProvider.class).to(GuiceComponentProvider.class).in(Scopes.SINGLETON);

        final Provider<Injector> injectorProvider = getProvider(Injector.class);
        final Set<Class<?>> injectables = classScanner.getTypesAnnotatedWith(Singleton.class);
        int boundedComponents = 0;
        for (final Class<?> clazz : injectables) {
            if (isInstantiableClass(clazz)) {
                LOG.debug("Binding component: {}", clazz.getName());
                bind(clazz).in(Scopes.SINGLETON);
                registerBeans(clazz, injectorProvider);
                boundedComponents++;
            }
        }
        LOG.info("Successfully bound {} components.", boundedComponents);
    }

    @SuppressWarnings("unchecked")
    private void registerBeans(Class<?> clazz, Provider<Injector> injectorProvider) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Produces.class)) {
                continue;
            }
            final Class<Object> returnType = (Class<Object>) method.getReturnType();
            if (returnType.equals(void.class)) {
                throw new IllegalStateException("@Produces method cannot return void: " +
                    method.getName());
            }
            bind(returnType)
                .toProvider(new BeanProvider<>(clazz, method, injectorProvider))
                .in(Scopes.SINGLETON);
            LOG.debug("Bound @Produces method: {} from {}.{}()",
                returnType.getSimpleName(), clazz.getSimpleName(), method.getName());
        }
    }

    private boolean isInstantiableClass(Class<?> clazz) {
        return !clazz.isInterface()
            && !clazz.isAnnotation()
            && !clazz.isEnum()
            && !Modifier.isAbstract(clazz.getModifiers());
    }
}
