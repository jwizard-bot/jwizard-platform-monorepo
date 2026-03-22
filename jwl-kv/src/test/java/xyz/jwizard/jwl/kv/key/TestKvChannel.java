package xyz.jwizard.jwl.kv.key;

public enum TestKvChannel implements KvChannel {
    TEST_EVENTS("test:channel:events"),
    USER_NOTIFICATIONS("user:%s:notifications"),
    ;

    private final String pattern;

    TestKvChannel(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String buildChannel(Object... params) {
        return String.format(pattern, params);
    }
}
