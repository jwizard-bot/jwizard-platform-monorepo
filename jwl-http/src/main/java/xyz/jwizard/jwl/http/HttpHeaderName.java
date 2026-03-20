package xyz.jwizard.jwl.http;

public enum HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    ;

    private final String key;

    HttpHeaderName(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
