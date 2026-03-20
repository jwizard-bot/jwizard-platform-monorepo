package xyz.jwizard.jwl.http.filter;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.HttpStatus;
import xyz.jwizard.jwl.http.Secured;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.header.TestHttpHeaderValue;

import static xyz.jwizard.jwl.http.HttpServerIntegrationTest.TEST_PASSWORD;

@Singleton
public class AnnotationSecurityFilter implements HttpFilter {
    @Override
    public boolean supports(xyz.jwizard.jwl.http.route.Route route) {
        return route.method().isAnnotationPresent(Secured.class);
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        final String token = req.getHeader(TestHttpHeaderName.AUTHORIZATION);
        if (!TEST_PASSWORD.equals(token)) {
            res.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }
        res.setHeader(TestHttpHeaderName.X_SECURED_BY, TestHttpHeaderValue.ANNOTATION_FILTER);
        return true;
    }

    @Override
    public int order() {
        return -50;
    }

}
