package xyz.jwizard.jwl.common.di;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import jakarta.inject.Inject;
import xyz.jwizard.jwl.common.reflect.TypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

public class GuiceComponentProvider implements ComponentProvider {
    private final Injector injector;

    @Inject
    public GuiceComponentProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public Collection<Object> getInstancesAnnotatedWith(Class<? extends Annotation> annotation) {
        return injector.getAllBindings().keySet().stream()
            .map(Key::getTypeLiteral)
            .map(TypeLiteral::getRawType)
            .filter(clazz -> clazz.isAnnotationPresent(annotation))
            .map(injector::getInstance)
            .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getInstancesOf(Class<T> type) {
        return injector.getAllBindings().keySet().stream()
            .filter(key -> {
                final Class<?> boundRawType = key.getTypeLiteral().getRawType();
                return type.isAssignableFrom(boundRawType)
                    && !boundRawType.isInterface()
                    && !Modifier.isAbstract(boundRawType.getModifiers());
            })
            .map(key -> (T) injector.getInstance(key))
            .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getInstancesOf(TypeReference<T> typeReference) {
        final TypeLiteral<T> guiceTypeLiteral = (TypeLiteral<T>) TypeLiteral
            .get(typeReference.getType());
        final Class<T> rawType = (Class<T>) guiceTypeLiteral.getRawType();
        return getInstancesOf(rawType);
    }
}
