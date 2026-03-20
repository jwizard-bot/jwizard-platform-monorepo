package xyz.jwizard.jwl.http.filter;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.route.Route;

import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class CacheSpyFilter implements HttpFilter {
    public static final AtomicInteger supportsCounter = new AtomicInteger(0);

    @Override
    public boolean supports(Route route) {
        if ("/api/map".equals(route.path())) {
            supportsCounter.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        return true;
    }

    @Override
    public int order() {
        return 100;
    }
}
