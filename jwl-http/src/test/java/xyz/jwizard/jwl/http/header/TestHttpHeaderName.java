package xyz.jwizard.jwl.http.header;

public enum TestHttpHeaderName implements HttpHeaderName {
    AUTHORIZATION("Authorization"),
    X_SECURED_BY("X-Secured-By"),
    X_TEST_FILTER("X-Test-Filter"),
    X_FILTER_ORDER("X-Filter-Order"),
    ;

    private final String key;

    TestHttpHeaderName(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
