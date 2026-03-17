package xyz.jwizard.jwl.transport.http.route;

import java.util.Map;

public record MatchResult(Route route, Map<String, String> variables) {
}
