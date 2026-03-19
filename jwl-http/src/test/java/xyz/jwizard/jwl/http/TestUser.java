package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.annotation.validation.Length;
import xyz.jwizard.jwl.http.annotation.validation.NotNull;
import xyz.jwizard.jwl.http.annotation.validation.Range;

public record TestUser(
    @NotNull @Length(min = 3) String name,
    @Range(min = 18) int age
) {
}
