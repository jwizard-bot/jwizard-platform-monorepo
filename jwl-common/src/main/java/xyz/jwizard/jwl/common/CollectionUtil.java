package xyz.jwizard.jwl.common;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class CollectionUtil {
    @SafeVarargs
    public static <T> LinkedHashSet<T> linkedSetOf(T... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }
}
