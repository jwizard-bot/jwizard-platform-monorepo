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
package xyz.jwizard.jwl.common.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public class ProviderCache<K, C, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Set<V> providers;
    private final BiPredicate<V, C> supportsPredicate;

    public ProviderCache(Set<V> providers, BiPredicate<V, C> supportsPredicate) {
        this.providers = providers;
        this.supportsPredicate = supportsPredicate;
    }

    public V get(K key, C context) {
        if (key == null) {
            return null;
        }
        return cache.computeIfAbsent(key, k ->
            providers.stream()
                .filter(p -> supportsPredicate.test(p, context))
                .findFirst()
                .orElse(null)
        );
    }
}
