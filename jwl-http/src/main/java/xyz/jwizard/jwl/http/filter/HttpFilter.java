package xyz.jwizard.jwl.http.filter;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.route.Route;

public interface HttpFilter {
    boolean supports(Route route);

    // true = go forward, false = stop request
    boolean preHandle(HttpRequest req, HttpResponse res) throws Exception;

    // lower = sooner
    default int order() {
        return 0;
    }
}
