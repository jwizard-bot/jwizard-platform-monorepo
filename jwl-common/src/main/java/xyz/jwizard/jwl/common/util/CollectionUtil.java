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
package xyz.jwizard.jwl.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class CollectionUtil {
    private CollectionUtil() {
        throw new ForbiddenInstantiationException(CollectionUtil.class);
    }

    @SafeVarargs
    public static <T> LinkedHashSet<T> linkedSetOf(T... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <T> List<T> listOf(T first, T... rest) {
        if (first == null && (rest == null || rest.length == 0)) {
            return Collections.emptyList();
        }
        final List<T> result = new ArrayList<>();
        if (first != null) {
            result.add(first);
        }
        if (rest != null) {
            Collections.addAll(result, rest);
        }
        return Collections.unmodifiableList(result);
    }
}
