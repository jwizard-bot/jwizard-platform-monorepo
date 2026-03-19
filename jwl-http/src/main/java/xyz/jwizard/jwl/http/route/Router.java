package xyz.jwizard.jwl.http.route;

public interface Router {
    void addRoute(String method, String path, Route route);

    MatchResult findRoute(String method, String path);
}
