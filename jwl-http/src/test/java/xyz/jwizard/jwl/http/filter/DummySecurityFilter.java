package xyz.jwizard.jwl.http.filter;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.header.TestHttpHeaderValue;
import xyz.jwizard.jwl.http.route.Route;

@Singleton
public class DummySecurityFilter implements HttpFilter {
    @Override
    public boolean supports(Route route) {
        return true;
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        res.setHeader(TestHttpHeaderName.X_TEST_FILTER, TestHttpHeaderValue.EXECUTED);
        if ("/api/blocked".equals(req.getPath())) {
            res.setStatus(HttpStatus.FORBIDDEN_403);
            return false;
        }
        return true;
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
