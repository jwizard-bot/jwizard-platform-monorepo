package xyz.jwizard.jwl.http;

public enum HttpHeader {
    APPLICATION_JSON_UTF_8("application/json; charset=utf-8"),
    TEXT_PLAIN_UTF_8("text/plain; charset=utf-8"),
    ;

    private final String key;

    HttpHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
