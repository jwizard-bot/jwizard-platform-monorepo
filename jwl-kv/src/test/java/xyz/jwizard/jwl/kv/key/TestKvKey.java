package xyz.jwizard.jwl.kv.key;

public enum TestKvKey implements KvKey {
    USER_PROFILE("user:%s:name", 0),
    TEMP_SESSION("temp:key", 3),
    ;

    private final String pattern;
    private final long ttl;

    TestKvKey(String pattern, long ttl) {
        this.pattern = pattern;
        this.ttl = ttl;
    }

    @Override
    public String build(Object... params) {
        return String.format(pattern, params);
    }

    @Override
    public long getDefaultTtlSeconds() {
        return ttl;
    }
}
