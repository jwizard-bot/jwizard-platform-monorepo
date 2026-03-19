package xyz.jwizard.jwl.common.util;

import java.lang.reflect.Array;
import java.util.Collection;

public class ArrayUtil {
    private ArrayUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<T> clazz) {
        if (collection == null) {
            return (T[]) Array.newInstance(clazz, 0);
        }
        final T[] array = (T[]) Array.newInstance(clazz, collection.size());
        return collection.toArray(array);
    }
}
