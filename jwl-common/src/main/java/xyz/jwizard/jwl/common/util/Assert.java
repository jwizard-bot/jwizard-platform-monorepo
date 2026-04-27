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

import java.util.Collection;
import java.util.Objects;

import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class Assert {
    private Assert() {
        throw new ForbiddenInstantiationException(Assert.class);
    }

    public static <T> void notNull(T object, String message) {
        state(object != null, message);
    }

    public static void notEmpty(Collection<?> collection, String message) {
        state(collection != null && !collection.isEmpty(), message);
    }

    public static void notNullAll(Collection<?> collection, String message) {
        state(collection != null && collection.stream().allMatch(Objects::nonNull), message);
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new CriticalBootstrapException(message);
        }
    }
}
