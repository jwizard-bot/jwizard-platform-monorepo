package xyz.jwizard.jwl.kv.key;

public interface KvKey {
    String build(Object... params);

    // 0 = key life forever
    long getDefaultTtlSeconds();
}
