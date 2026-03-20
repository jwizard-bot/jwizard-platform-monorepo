package xyz.jwizard.jwl.http.header;

public enum CommonHttpHeaderValue implements HttpHeaderValue {
    APPLICATION_JSON_UTF_8("application/json; charset=utf-8"),
    TEXT_PLAIN_UTF_8("text/plain; charset=utf-8"),
    ;

    private final String key;

    CommonHttpHeaderValue(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
