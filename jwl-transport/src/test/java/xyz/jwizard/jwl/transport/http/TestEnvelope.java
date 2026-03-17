package xyz.jwizard.jwl.transport.http;

import xyz.jwizard.jwl.transport.http.annotation.validation.NestedValid;
import xyz.jwizard.jwl.transport.http.annotation.validation.NotNull;

public record TestEnvelope(
    @NotNull String requestId,
    @NestedValid TestUser testUser
) {
}
