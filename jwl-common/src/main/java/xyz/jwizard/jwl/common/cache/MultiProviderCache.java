package xyz.jwizard.jwl.common.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public class MultiProviderCache<K, C, V> {
    private final Map<K, List<V>> cache = new ConcurrentHashMap<>();
    private final List<V> providers;
    private final BiPredicate<V, C> supportsPredicate;

    public MultiProviderCache(List<V> providers, BiPredicate<V, C> supportsPredicate) {
        this.providers = providers;
        this.supportsPredicate = supportsPredicate;
    }

    public List<V> get(K key, C context) {
        if (key == null) {
            return List.of();
        }
        return cache.computeIfAbsent(key, k ->
            providers.stream()
                .filter(p -> supportsPredicate.test(p, context))
                .toList()
        );
    }
}
