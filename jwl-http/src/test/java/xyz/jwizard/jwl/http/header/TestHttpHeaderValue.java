package xyz.jwizard.jwl.http.header;

public enum TestHttpHeaderValue implements HttpHeaderValue {
    ANNOTATION_FILTER("AnnotationFilter"),
    EXECUTED("Executed"),
    ;

    private final String key;

    TestHttpHeaderValue(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
