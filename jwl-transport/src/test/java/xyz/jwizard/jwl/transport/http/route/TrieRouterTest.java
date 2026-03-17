package xyz.jwizard.jwl.transport.http.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class TrieRouterTest {
    private TrieRouter router;
    private Route mockRoute;

    @BeforeEach
    void setUp() {
        router = new TrieRouter();
        mockRoute = Mockito.mock(Route.class);
    }

    @Test
    @DisplayName("should match simple static route")
    void shouldMatchStaticRoute() {
        // given
        router.addRoute("GET", "/api/users", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users");
        // then
        assertThat(result).isNotNull();
        assertThat(result.route()).isEqualTo(mockRoute);
        assertThat(result.variables()).isEmpty();
    }

    @Test
    @DisplayName("should match route with path variable and extract it")
    void shouldExtractPathVariable() {
        // given
        router.addRoute("GET", "/api/users/{id}", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users/123");
        // then
        assertThat(result).isNotNull();
        assertThat(result.route()).isEqualTo(mockRoute);
        assertThat(result.variables())
            .containsEntry("id", "123")
            .hasSize(1);
    }

    @Test
    @DisplayName("should match complex route with multiple variables")
    void shouldExtractMultipleVariables() {
        // given
        router.addRoute("GET", "/api/users/{userId}/orders/{orderId}", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users/jwizard/orders/99");
        // then
        assertThat(result).isNotNull();
        assertThat(result.variables())
            .containsEntry("userId", "jwizard")
            .containsEntry("orderId", "99");
    }

    @Test
    @DisplayName("should return null when path does not match")
    void shouldNotMatchWrongPath() {
        // given
        router.addRoute("GET", "/api/users", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/admins");
        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return null when method does not match")
    void shouldNotMatchWrongMethod() {
        // given
        router.addRoute("POST", "/api/users", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users");
        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should prefer static path over variable path (shadowing)")
    void shouldPreferStaticPath() {
        // given
        final Route staticRoute = Mockito.mock(Route.class);
        final Route varRoute = Mockito.mock(Route.class);
        router.addRoute("GET", "/api/users/me", staticRoute);
        router.addRoute("GET", "/api/users/{id}", varRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users/me");
        // then
        assertThat(result.route()).isEqualTo(staticRoute);
    }

    @Test
    @DisplayName("should handle trailing slashes correctly")
    void shouldHandleTrailingSlashes() {
        // given
        router.addRoute("GET", "/api/users/", mockRoute);
        // when
        final MatchResult result = router.findRoute("GET", "/api/users");
        // then
        assertThat(result).isNotNull();
        assertThat(result.route()).isEqualTo(mockRoute);
    }
}
