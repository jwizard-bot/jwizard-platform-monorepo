package xyz.jwizard.jwl.http;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.common.json.JacksonSerializer;
import xyz.jwizard.jwl.common.json.JsonSerializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HttpServerIntegrationTest {
    private static int dynamicPort;
    private static HttpServer httpServer;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final JsonSerializer jsonSerializer = new JacksonSerializer();

    @BeforeAll
    static void startServer() {
        httpServer = HttpServer.builder()
            .port(0)
            .basePackageToScan("xyz.jwizard.jwl.transport.http")
            .blockingMode(false)
            .build();
        httpServer.start();
        dynamicPort = httpServer.getLocalPort();
    }

    @AfterAll
    static void stopServer() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }

    private HttpResponse<String> get(String path) throws Exception {
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + dynamicPort + path))
            .GET()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(Object body) throws Exception {
        final String json = jsonSerializer.serialize(body);
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
        final TestEnvelope payload = new TestEnvelope("REQ-001", new TestUser("Jwizard", 25));
        // when
        final HttpResponse<String> response = post(payload);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).contains("Success: Jwizard");
    }

    @Test
    @DisplayName("POST /api/test should return 400 Bad Request when validation fails (age < 18)")
    void shouldReturn400ForInvalidData() throws Exception {
        // given
        final TestEnvelope payload = new TestEnvelope("REQ-002", new TestUser("Jo", 10));
        // when
        final HttpResponse<String> response = post(payload);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(response.body()).isEmpty();
    }

    @Test
    @DisplayName("GET /non-existing should return 404 Not Found for non-existing route")
    void shouldReturn404() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/non-existing");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND_404);
    }

    @Test
    @DisplayName("GET /api/users/{id} should resolve path variable")
    void shouldResolvePathVariable() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/users/12345");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).isEqualTo("User ID: 12345");
    }

    @Test
    @DisplayName("GET /api/search should resolve multiple query parameters")
    void shouldResolveQueryParams() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/search?q=java&page=1");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).isEqualTo("Search: java on page 1");
    }

    @Test
    @DisplayName("GET /api/products should use default value 'all' when param is missing")
    void shouldUseCaseDefaultValue() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/products");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).isEqualTo("Category: all");
    }

    @Test
    @DisplayName("GET /api/profile should allow null when required is false")
    void shouldAllowOptionalParam() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/profile");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).isEqualTo("Status: Guest");
    }

    @Test
    @DisplayName("GET /api/items should parse default string '10' to int")
    void shouldParseDefaultInt() throws Exception {
        // given & when
        final HttpResponse<String> response = get("/api/items");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(response.body()).isEqualTo("Limit: 10");
    }
}
