package xyz.jwizard.jwl.http;

public enum HttpStatus {
    OK_200(200),
    NO_CONTENT_204(204),
    BAD_REQUEST_400(400),
    UNAUTHORIZED_401(401),
    FORBIDDEN_403(403),
    NOT_FOUND_404(404),
    INTERNAL_SERVER_ERROR_500(500),
    ;

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
