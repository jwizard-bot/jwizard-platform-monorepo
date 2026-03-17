package xyz.jwizard.jwl.transport.http;

import xyz.jwizard.jwl.transport.http.annotation.validation.Length;
import xyz.jwizard.jwl.transport.http.annotation.validation.NotNull;
import xyz.jwizard.jwl.transport.http.annotation.validation.Range;

public record TestUser(
    @NotNull @Length(min = 3) String name,
    @Range(min = 18) int age
) {
}
