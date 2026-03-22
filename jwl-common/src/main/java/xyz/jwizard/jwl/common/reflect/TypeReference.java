package xyz.jwizard.jwl.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {
    private final Type type;

    protected TypeReference() {
        final Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new IllegalArgumentException("TypeReference requires a type parameter");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
