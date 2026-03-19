package xyz.jwizard.jwl.common.reflect;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ClassScanner extends AutoCloseable {
    Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);

    <T> Set<Class<? extends T>> getSubtypesOf(Class<T> type);
}
