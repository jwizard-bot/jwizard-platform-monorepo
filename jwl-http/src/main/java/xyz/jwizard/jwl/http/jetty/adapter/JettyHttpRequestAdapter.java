package xyz.jwizard.jwl.http.jetty.adapter;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.header.HttpHeaderName;

import java.io.InputStream;

public class JettyHttpRequestAdapter implements HttpRequest {
    private final Request request;
    private MultiMap<String> queryParams;

    public JettyHttpRequestAdapter(Request request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPath() {
        return request.getHttpURI().getPath();
    }

    @Override
    public long getLength() {
        return request.getLength();
    }

    @Override
    public InputStream getInputStream() {
        return Content.Source.asInputStream(request);
    }

    @Override
    public String getQuery() {
        return request.getHttpURI().getQuery();
    }

    @Override
    public String getQueryParam(String name) {
        if (queryParams == null) {
            final String query = getQuery();
            if (query != null && !query.isEmpty()) {
                queryParams = UrlEncoded.decodeQuery(query);
            } else {
                queryParams = new MultiMap<>();
            }
        }
        return queryParams.getValue(name);
    }

    @Override
    public String getHeader(HttpHeaderName name) {
        return getHeaderUnsafe(name.getKey());
    }

    @Override
    public String getHeaderUnsafe(String name) {
        return request.getHeaders().get(name);
    }
}
