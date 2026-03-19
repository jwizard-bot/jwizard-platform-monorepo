package xyz.jwizard.jwl.http.route;

import java.util.Set;

public interface Router {
    void addRoute(String method, String path, Route route);

    MatchResult findRoute(String method, String path);

    Set<String> getVariableNamesFor(String method, String path);
}
