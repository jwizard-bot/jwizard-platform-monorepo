package xyz.jwizard.jwl.http;

import xyz.jwizard.jwl.http.annotation.*;

import java.util.Map;

@HttpController
public class IntegrationTestController {
    @RequestMapping(value = "/api/test", method = HttpMethod.POST)
    public ResponseEntity<String> handleTest(@Body TestEnvelope testEnvelope) {
        return ResponseEntity.ok("Success: " + testEnvelope.testUser().name());
    }

    @RequestMapping(value = "/api/users/{id}", method = HttpMethod.GET)
    public ResponseEntity<String> getUserById(@PathVariable("id") String userId) {
        return ResponseEntity.ok("User ID: " + userId);
    }

    @RequestMapping(value = "/api/search", method = HttpMethod.GET)
    public ResponseEntity<String> search(
        @RequestParam("q") String query,
        @RequestParam("page") Integer page) {
        return ResponseEntity.ok("Search: " + query + " on page " + page);
    }

    @RequestMapping(value = "/api/users/{id}/orders", method = HttpMethod.GET)
    public ResponseEntity<String> getUserOrders(
        @PathVariable("id") String userId,
        @RequestParam("status") String status) {
        return ResponseEntity.ok("User " + userId + " orders with status " + status);
    }

    @RequestMapping(value = "/api/products", method = HttpMethod.GET)
    public ResponseEntity<String> getProducts(
        @RequestParam(value = "category", defaultValue = "all") String category) {
        return ResponseEntity.ok("Category: " + category);
    }

    @RequestMapping(value = "/api/profile", method = HttpMethod.GET)
    public ResponseEntity<String> getProfile(
        @RequestParam(value = "token", required = false) String token) {
        String status = (token == null) ? "Guest" : "User-" + token;
        return ResponseEntity.ok("Status: " + status);
    }

    @RequestMapping(value = "/api/items", method = HttpMethod.GET)
    public ResponseEntity<String> getItems(
        @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return ResponseEntity.ok("Limit: " + limit);
    }

    @RequestMapping(value = "/api/map", method = HttpMethod.GET)
    public ResponseEntity<Map<String, Object>> getMap() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "version", "1.0.0",
            "active", true
        ));
    }
}
