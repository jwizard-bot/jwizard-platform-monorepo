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
package xyz.jwizard.jwl.http.resolver.body;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.SerializerFormat;
import xyz.jwizard.jwl.codec.serialization.StandardSerializerFormat;
import xyz.jwizard.jwl.common.util.CollectionUtil;
import xyz.jwizard.jwl.common.util.math.MemSize;
import xyz.jwizard.jwl.common.util.math.MemUnit;

enum BodyMediaSerializer {
    JSON(
        StandardSerializerFormat.JSON,
        true,
        MemSize.of(2, MemUnit.MB),
        null
    ),
    PROTOBUF(
        StandardSerializerFormat.PROTOBUF,
        false,
        MemSize.of(1, MemUnit.MB),
        null,
        "application/x-protobuf"
    ),
    RAW(
        StandardSerializerFormat.RAW,
        false,
        MemSize.of(5, MemUnit.MB),
        byte[].class,
        "image/*"
    ),
    ;

    private static final Logger LOG = LoggerFactory.getLogger(BodyMediaSerializer.class);
    private static final BodyMediaSerializer DEFAULT_MAPPING = JSON;

    // for fast O(1) search
    private static final Map<Class<?>, BodyMediaSerializer> BY_CLASS = new HashMap<>();
    private static final Map<String, BodyMediaSerializer> BY_EXACT_TYPE = new HashMap<>();
    private static final Map<String, BodyMediaSerializer> BY_PREFIX_TYPE = new HashMap<>();

    static {
        for (final BodyMediaSerializer mapping : values()) {
            registerClassMapping(mapping);
            registerContentTypeMappings(mapping);
        }
        LOG.info("BodyMediaSerializer cache initialized ({} class, {} exact, {} wildcard mappings)",
            BY_CLASS.size(), BY_EXACT_TYPE.size(), BY_PREFIX_TYPE.size());
    }

    private final SerializerFormat format;
    private final boolean validate;
    private final long maxSizeBytes;
    private final Class<?> targetClass;
    private final List<String> contentTypes;

    BodyMediaSerializer(SerializerFormat format, boolean validate, long maxSizeBytes,
                        Class<?> targetClass, String... contentTypes) {
        this.format = format;
        this.validate = validate;
        this.maxSizeBytes = maxSizeBytes;
        this.targetClass = targetClass;
        this.contentTypes = CollectionUtil.listOf(format.getMimeType(), contentTypes);
    }

    static BodyMediaSerializer resolve(Class<?> targetType, String contentType) {
        final BodyMediaSerializer classMapping = BY_CLASS.get(targetType);
        if (classMapping != null) {
            return classMapping;
        }
        if (contentType == null) {
            return DEFAULT_MAPPING;
        }
        final BodyMediaSerializer exactMapping = BY_EXACT_TYPE.get(contentType);
        if (exactMapping != null) {
            return exactMapping;
        }
        final int slashIdx = contentType.indexOf('/');
        if (slashIdx != -1) {
            final String prefix = contentType.substring(0, slashIdx + 1);
            final BodyMediaSerializer prefixMapping = BY_PREFIX_TYPE.get(prefix);
            if (prefixMapping != null) {
                return prefixMapping;
            }
        }
        return DEFAULT_MAPPING;
    }

    private static void registerClassMapping(BodyMediaSerializer mapping) {
        if (mapping.targetClass != null) {
            BY_CLASS.put(mapping.targetClass, mapping);
            LOG.trace("Registered class mapping: {} -> {}",
                mapping.targetClass.getSimpleName(), mapping.name());
        }
    }

    private static void registerContentTypeMappings(BodyMediaSerializer mapping) {
        if (mapping.contentTypes == null || mapping.contentTypes.isEmpty()) {
            return;
        }
        for (final String type : mapping.contentTypes) {
            final String normalizedType = type.toLowerCase();
            if (normalizedType.endsWith("/*")) {
                final String prefix = normalizedType.substring(0, normalizedType.length() - 1);
                BY_PREFIX_TYPE.put(prefix, mapping);
                LOG.trace("Registered wildcard mapping: {}* -> {}", prefix, mapping.name());
            } else {
                BY_EXACT_TYPE.put(normalizedType, mapping);
                LOG.trace("Registered exact mapping: {} -> {}", normalizedType, mapping.name());
            }
        }
    }

    SerializerFormat getFormat() {
        return format;
    }

    boolean isValidate() {
        return validate;
    }

    long getMaxSizeBytes() {
        return maxSizeBytes;
    }
}
