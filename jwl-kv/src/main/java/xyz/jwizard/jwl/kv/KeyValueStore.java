package xyz.jwizard.jwl.kv;

import xyz.jwizard.jwl.kv.key.KvKey;

public interface KeyValueStore {
    void set(KvKey key, String value, Object... keyParams);

    void setWithTtl(KvKey key, String value, Object... keyParams);

    String get(KvKey key, Object... keyParams);

    void del(KvKey key, Object... keyParams);
}
