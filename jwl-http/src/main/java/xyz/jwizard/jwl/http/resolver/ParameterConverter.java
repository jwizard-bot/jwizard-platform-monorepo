package xyz.jwizard.jwl.http.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

enum ParameterConverter {
    STRING(String.class, value -> value),
    INTEGER(Integer.class, Integer::valueOf),
    LONG(Long.class, Long::valueOf),
    BOOLEAN(Boolean.class, value -> {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid boolean value: '" + value +
            "'. Expected 'true' or 'false'.");
    }),
    DOUBLE(Double.class, Double::valueOf),
    ;

    // for fast O(1) search
    private static final Map<Class<?>, ParameterConverter> LOOKUP = new HashMap<>();

    static {
        for (final ParameterConverter converter : values()) {
            LOOKUP.put(converter.targetType, converter);
        }
    }

    private final Class<?> targetType;
    private final Function<String, Object> converterFunction;

    ParameterConverter(Class<?> targetType, Function<String, Object> converterFunction) {
        this.targetType = targetType;
        this.converterFunction = converterFunction;
    }

    static Object parse(Class<?> targetType, String value) {
        if (value == null) {
            return null;
        }
        final ParameterConverter converter = LOOKUP.get(targetType);
        if (converter == null) {
            throw new IllegalArgumentException("Unsupported argument type: " +
                targetType.getName());
        }
        return converter.converterFunction.apply(value);
    }
}
