package xyz.jwizard.jwl.common.di;

import xyz.jwizard.jwl.common.reflect.TypeReference;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface ComponentProvider {
    <T> T getInstance(Class<T> clazz);

    Collection<Object> getInstancesAnnotatedWith(Class<? extends Annotation> annotation);

    <T> Collection<T> getInstancesOf(Class<T> type);

    <T> Collection<T> getInstancesOf(TypeReference<T> typeReference);
}
