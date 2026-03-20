package xyz.jwizard.jwl.http.header;

public enum CommonHttpHeaderName implements HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    ;

    private final String key;

    CommonHttpHeaderName(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
