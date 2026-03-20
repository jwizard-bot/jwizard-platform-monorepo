package xyz.jwizard.jwl.http;

public record ResponseEntity<T>(HttpStatus status, T body) {
    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(HttpStatus.OK_200, body);
    }

    public static <T> ResponseEntity<T> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT_204, null);
    }
}
