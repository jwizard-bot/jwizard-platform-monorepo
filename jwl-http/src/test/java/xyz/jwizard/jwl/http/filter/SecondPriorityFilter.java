package xyz.jwizard.jwl.http.filter;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.route.Route;

@Singleton
public class SecondPriorityFilter implements HttpFilter {
    @Override
    public boolean supports(Route route) {
        return "/api/public".equals(route.path());
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        final String current = res.getHeader(TestHttpHeaderName.X_FILTER_ORDER);
        res.setHeader(TestHttpHeaderName.X_FILTER_ORDER,
            (current == null ? "" : current) + " -> Second");
        return true;
    }

    @Override
    public int order() {
        return 2;
    }
}
