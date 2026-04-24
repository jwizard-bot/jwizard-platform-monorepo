/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.http.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            "', expected 'true' or 'false'");
    }),
    DOUBLE(Double.class, Double::valueOf),
    ;

    private static final Logger LOG = LoggerFactory.getLogger(ParameterConverter.class);

    // for fast O(1) search
    private static final Map<Class<?>, ParameterConverter> LOOKUP = new HashMap<>();

    static {
        for (final ParameterConverter converter : values()) {
            LOOKUP.put(converter.targetType, converter);
            LOG.trace("Registered parameter converter: {} -> {}",
                converter.targetType.getSimpleName(), converter.name());
        }
        LOG.info("ParameterConverter cache initialized with {} mapping(s)", LOOKUP.size());
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
