/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.http;

import java.util.Map;

import xyz.jwizard.jwl.common.util.math.MemUnit;
import xyz.jwizard.jwl.http.annotation.Body;
import xyz.jwizard.jwl.http.annotation.HttpController;
import xyz.jwizard.jwl.http.annotation.HttpMethod;
import xyz.jwizard.jwl.http.annotation.PathVariable;
import xyz.jwizard.jwl.http.annotation.RequestMapping;
import xyz.jwizard.jwl.http.annotation.RequestParam;
import xyz.jwizard.jwl.http.annotation.SecuredRoute;

@HttpController
class IntegrationTestController {
    @RequestMapping(value = "/api/test", method = HttpMethod.POST)
    ResponseEntity<String> handleTest(@Body TestEnvelope testEnvelope) {
        return ResponseEntity.ok("Success: " + testEnvelope.testUser().name());
    }

    @RequestMapping(value = "/api/raw", method = HttpMethod.POST)
    ResponseEntity<String> handleRawData(@Body byte[] data) {
        return ResponseEntity.ok("Received " + data.length + " bytes of raw data");
    }

    @RequestMapping(value = "/api/limited", method = HttpMethod.POST)
    ResponseEntity<String> handleLimitedData(@Body(limit = 10, unit = MemUnit.BYTES) byte[] data) {
        return ResponseEntity.ok("Processed limited payload");
    }

    @RequestMapping(value = "/api/users/{id}", method = HttpMethod.GET)
    ResponseEntity<String> getUserById(@PathVariable("id") String userId) {
        return ResponseEntity.ok("User ID: " + userId);
    }

    @RequestMapping(value = "/api/search", method = HttpMethod.GET)
    ResponseEntity<String> search(
        @RequestParam("q") String query,
        @RequestParam("page") Integer page) {
        return ResponseEntity.ok("Search: " + query + " on page " + page);
    }

    @RequestMapping(value = "/api/users/{id}/orders", method = HttpMethod.GET)
    ResponseEntity<String> getUserOrders(
        @PathVariable("id") String userId,
        @RequestParam("status") String status) {
        return ResponseEntity.ok("User " + userId + " orders with status " + status);
    }

    @RequestMapping(value = "/api/products", method = HttpMethod.GET)
    ResponseEntity<String> getProducts(
        @RequestParam(value = "category", defaultValue = "all") String category) {
        return ResponseEntity.ok("Category: " + category);
    }

    @RequestMapping(value = "/api/profile", method = HttpMethod.GET)
    ResponseEntity<String> getProfile(
        @RequestParam(value = "token", required = false) String token) {
        final String status = (token == null) ? "Guest" : "User-" + token;
        return ResponseEntity.ok("Status: " + status);
    }

    @RequestMapping(value = "/api/items", method = HttpMethod.GET)
    ResponseEntity<String> getItems(
        @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return ResponseEntity.ok("Limit: " + limit);
    }

    @RequestMapping(value = "/api/map", method = HttpMethod.GET)
    ResponseEntity<Map<String, Object>> getMap() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "version", "1.0.0",
            "active", true
        ));
    }

    @RequestMapping(value = "/api/blocked", method = HttpMethod.GET)
    ResponseEntity<String> blockedEndpoint() {
        return ResponseEntity.ok("blocked");
    }

    @RequestMapping(value = "/api/public", method = HttpMethod.GET)
    ResponseEntity<String> openEndpoint() {
        return ResponseEntity.ok("public data");
    }

    @SecuredRoute
    @RequestMapping(value = "/api/private", method = HttpMethod.GET)
    ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("secret data");
    }
}
