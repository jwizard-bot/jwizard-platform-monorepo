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

import xyz.jwizard.jwl.common.reflect.TypeReference;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface ComponentProvider {
    <T> T getInstance(Class<T> clazz);

    Collection<Object> getInstancesAnnotatedWith(Class<? extends Annotation> annotation);

    <T> Collection<T> getInstancesOf(Class<T> type);

    <T> Collection<T> getInstancesOf(TypeReference<T> typeReference);
}
