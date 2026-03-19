package xyz.jwizard.jwl.common.di;

import com.google.inject.Injector;
import com.google.inject.Provider;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;

import java.lang.reflect.Method;

class BeanProvider<T> implements Provider<T> {
    private final Class<?> configClass;
    private final Method method;
    private final Provider<Injector> injectorProvider;

    BeanProvider(Class<?> configClass, Method method, Provider<Injector> injectorProvider) {
        this.configClass = configClass;
        this.method = method;
        this.injectorProvider = injectorProvider;
        this.method.setAccessible(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            final Injector injector = injectorProvider.get();
            final Object configInstance = injector.getInstance(configClass);
            final Class<?>[] paramTypes = method.getParameterTypes();
            final Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                args[i] = injector.getInstance(paramTypes[i]);
            }
            return (T) method.invoke(configInstance, args);
        } catch (Exception ex) {
            throw new CriticalBootstrapException(
                "Failed to instantiate @Bean method: " + method.getName() + " in " +
                    configClass.getSimpleName(), ex
            );
        }
    }
}
