package xyz.jwizard.jwl.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.common.di.ApplicationContext;
import xyz.jwizard.jwl.common.json.JacksonSerializer;
import xyz.jwizard.jwl.common.json.JsonSerializer;
import xyz.jwizard.jwl.common.reflect.ClassGraphScanner;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.http.filter.CacheSpyFilter;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.header.TestHttpHeaderValue;
import xyz.jwizard.jwl.http.jetty.JettyHttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HttpServerIntegrationTest {
    public static final String TEST_PASSWORD = "SecretToken-123";

    private static final JsonSerializer JSON_SERIALIZER = new JacksonSerializer();
    private static HttpServer httpServer;
    private static int dynamicPort;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    static void startServer() {
        final String packageName = HttpServerIntegrationTest.class.getPackageName();
        final ClassScanner scanner = new ClassGraphScanner(packageName);
        final ApplicationContext context = new ApplicationContext(scanner);
        httpServer = JettyHttpServer.builder()
            .componentProvider(context.getProvider())
            .jsonSerializer(JSON_SERIALIZER)
            .port(0)
            .build();
        httpServer.start();
        dynamicPort = httpServer.getLocalPort();
    }

    @AfterAll
    static void stopServer() {
        IoUtil.closeQuietly(httpServer);
    }

    private HttpResponse<String> get(String path) throws Exception {
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + dynamicPort + path))
            .GET()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(Object body) throws Exception {
        final String json = JSON_SERIALIZER.serialize(body);
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + dynamicPort + "/api/test"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("POST /api/test should return 200 OK when valid JSON is sent")
    void shouldReturnOkForValidRequest() throws Exception {
        // given
        final TestEnvelope payload = new TestEnvelope("REQ-001", new TestUser("JWizard", 25));
        // when
        final HttpResponse<String> response = post(payload);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).contains("Success: JWizard");
    }

    @Test
    @DisplayName("POST /api/test should return 400 Bad Request when validation fails (age < 18)")
    void shouldReturn400ForInvalidData() throws Exception {
        // given
        final TestEnvelope payload = new TestEnvelope("REQ-002", new TestUser("Jo", 10));
        // when
        final HttpResponse<String> response = post(payload);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST_400.getCode());
        assertThat(response.body()).isEmpty();
    }

    @Test
    @DisplayName("GET /non-existing should return 404 Not Found for non-existing route")
    void shouldReturn404() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/non-existing");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND_404.getCode());
    }

    @Test
    @DisplayName("GET /api/users/{id} should resolve path variable")
    void shouldResolvePathVariable() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/users/12345");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("User ID: 12345");
    }

    @Test
    @DisplayName("GET /api/search should resolve multiple query parameters")
    void shouldResolveQueryParams() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/search?q=java&page=1");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("Search: java on page 1");
    }

    @Test
    @DisplayName("GET /api/products should use default value 'all' when param is missing")
    void shouldUseCaseDefaultValue() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/products");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("Category: all");
    }

    @Test
    @DisplayName("GET /api/profile should allow null when required is false")
    void shouldAllowOptionalParam() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/profile");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("Status: Guest");
    }

    @Test
    @DisplayName("GET /api/items should parse default string '10' to int")
    void shouldParseDefaultInt() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/items");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("Limit: 10");
    }

    @Test
    @DisplayName("GET /api/map should return 200 OK and serialize Map to JSON")
    void shouldReturnMapAsJson() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/map");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        final String body = response.body();
        assertThat(body).isNotBlank();
        assertThat(body).contains("\"status\":\"UP\"");
    }

    @Test
    @DisplayName("GET /api/map should contain custom header added by HttpFilter")
    void shouldExecuteFilterAndAddHeader() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/map");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.headers().firstValue(TestHttpHeaderName.X_TEST_FILTER.getKey()))
            .isPresent().contains(TestHttpHeaderValue.EXECUTED.getKey());
    }

    @Test
    @DisplayName("GET /api/blocked should be intercepted and stopped by HttpFilter returning false")
    void shouldBlockRequestByFilter() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/blocked");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN_403.getCode());
        assertThat(response.body()).isEmpty();
        assertThat(response.headers().firstValue(TestHttpHeaderName.X_TEST_FILTER.getKey()))
            .isPresent().contains(TestHttpHeaderValue.EXECUTED.getKey());
    }

    @Test
    @DisplayName("GET /api/public should completely bypass AnnotationSecurityFilter")
    void shouldBypassFilterForUnannotatedRoute() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/public");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("public data");
        assertThat(response.headers().firstValue(TestHttpHeaderName.X_SECURED_BY.getKey()))
            .isEmpty();
    }

    @Test
    @DisplayName("GET /api/private should return 401 Unauthorized when token is missing")
    void shouldBlockSecuredRouteWithoutToken() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/private");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED_401.getCode());
        assertThat(response.body()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/private should return 200 OK when valid token is provided")
    void shouldAllowSecuredRouteWithToken() throws Exception {
        // given
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + dynamicPort + "/api/private"))
            .header(TestHttpHeaderName.AUTHORIZATION.getKey(), TEST_PASSWORD)
            .GET()
            .build();
        // when
        final HttpResponse<String> response = httpClient
            .send(request, HttpResponse.BodyHandlers.ofString());
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.body()).isEqualTo("secret data");
        assertThat(response.headers().firstValue(TestHttpHeaderName.X_SECURED_BY.getKey()))
            .isPresent().contains(TestHttpHeaderValue.ANNOTATION_FILTER.getKey());
    }

    @Test
    @DisplayName("filters should be executed in order based on their priority")
    void shouldExecuteFiltersInCorrectOrder() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/public");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200.getCode());
        assertThat(response.headers().firstValue(TestHttpHeaderName.X_FILTER_ORDER.getKey()))
            .isPresent()
            .contains("First -> Second");
    }

    @Test
    @DisplayName("filter should be resolved via supports() only once and then cached")
    void shouldCacheFilterSupportResult() throws Exception {
        CacheSpyFilter.supportsCounter.set(0);
        // given & when
        get("/api/map");
        final int countAfterFirstRequest = CacheSpyFilter.supportsCounter.get();
        assertThat(countAfterFirstRequest).isEqualTo(1);
        get("/api/map");
        final int countAfterSecondRequest = CacheSpyFilter.supportsCounter.get();
        // then
        assertThat(countAfterSecondRequest)
            .withFailMessage("Filter supports() was called again! Cache is not working.")
            .isEqualTo(1);
    }
}
