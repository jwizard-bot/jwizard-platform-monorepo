package xyz.jwizard.jwl.http.filter;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.route.Route;

@Singleton
public class FirstPriorityFilter implements HttpFilter {
    @Override
    public boolean supports(Route route) {
        return route.path().equals("/api/public");
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        res.setHeader(TestHttpHeaderName.X_FILTER_ORDER, "First");
        return true;
    }

    @Override
    public int order() {
        return 1;
    }
}
