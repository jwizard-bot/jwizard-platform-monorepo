package xyz.jwizard.jwl.http.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

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

    @Test
    @DisplayName("should extract variable names from complex path for validation")
    void shouldExtractVariableNamesFromComplexPath() {
        // given
        final Route mockRoute = Mockito.mock(Route.class);
        String method = "GET";
        String path = "/users/{userId}/posts/{postId}";
        router.addRoute(method, path, mockRoute);
        // when
        final Set<String> variableNames = router.getVariableNamesFor(method, path);
        // then
        assertThat(variableNames)
            .hasSize(2)
            .containsExactlyInAnyOrder("userId", "postId");
    }

    @Test
    @DisplayName("should return empty set for static path validation")
    void shouldReturnEmptySetForStaticPath() {
        // given
        router.addRoute("POST", "/auth/login", Mockito.mock(Route.class));
        // when
        final Set<String> variableNames = router.getVariableNamesFor("POST", "/auth/login");
        // Then
        assertThat(variableNames).isEmpty();
    }

    @Test
    @DisplayName("should return empty set if path does not exist in trie during validation")
    void shouldReturnEmptySetIfPathDoesNotExistInTrie() {
        // when
        final Set<String> variableNames = router.getVariableNamesFor("GET", "/unknown/path/{id}");
        // then
        assertThat(variableNames).isEmpty();
    }

    @Test
    @DisplayName("should correctly extract names from mixed static/variable path")
    void shouldHandleMixedPathCorrectly() {
        // given
        router.addRoute("GET", "/api/v1/resource/{resourceId}/details", Mockito.mock(Route.class));
        // when
        final Set<String> variableNames = router
            .getVariableNamesFor("GET", "/api/v1/resource/{resourceId}/details");
        // then
        assertThat(variableNames).containsExactly("resourceId");
    }
}
