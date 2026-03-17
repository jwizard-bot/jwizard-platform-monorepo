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
