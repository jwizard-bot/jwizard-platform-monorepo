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
package xyz.jwizard.jwl.common.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import xyz.jwizard.jwl.common.reflect.TypeReference;
import xyz.jwizard.jwl.common.util.CastUtil;

import jakarta.inject.Inject;

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
    public <T> Collection<T> getInstancesOf(Class<T> type) {
        return injector.getAllBindings().keySet().stream()
            .filter(key -> {
                final Class<?> boundRawType = key.getTypeLiteral().getRawType();
                return type.isAssignableFrom(boundRawType)
                    && !boundRawType.isInterface()
                    && !Modifier.isAbstract(boundRawType.getModifiers());
            })
            .map(key -> CastUtil.<T>unsafeCast(injector.getInstance(key)))
            .collect(Collectors.toSet());
    }

    @Override
    public <T> Collection<T> getInstancesOf(TypeReference<T> typeReference) {
        final TypeLiteral<T> guiceTypeLiteral = CastUtil.unsafeCast(TypeLiteral
            .get(typeReference.getType()));
        final Class<T> rawType = CastUtil.unsafeCast(guiceTypeLiteral.getRawType());
        return getInstancesOf(rawType);
    }
}
