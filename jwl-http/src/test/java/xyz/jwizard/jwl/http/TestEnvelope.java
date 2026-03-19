package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.annotation.validation.NestedValid;
import xyz.jwizard.jwl.http.annotation.validation.NotNull;

public record TestEnvelope(
    @NotNull String requestId,
    @NestedValid TestUser testUser
) {
}
