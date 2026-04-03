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
package xyz.jwizard.jwl.http.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieRouter implements Router {
    private static final Logger LOG = LoggerFactory.getLogger(TrieRouter.class);

    private static final String DELIMITER_START = "{";
    private static final String DELIMITER_END = "}";

    private final RouteNode root = new RouteNode();

    @Override
    public void addRoute(String method, String path, Route route) {
        final String[] parts = (method + path).split("/");
        RouteNode current = root;
        for (final String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (part.startsWith(DELIMITER_START) && part.endsWith(DELIMITER_END)) {
                if (current.getVariableChild() == null) {
                    current.setVariableName(part.substring(1, part.length() - 1));
                    current.setVariableChild(new RouteNode());
                }
                current = current.getVariableChild();
            } else {
                current.getStaticChildren().putIfAbsent(part, new RouteNode());
                current = current.getStaticChildren().get(part);
            }
        }
        if (current.getRoute() != null) {
            LOG.warn("Overwriting existing route: {} {}", method, path);
        }
        current.setRoute(route);
        LOG.info("Registered route: {} {}", method, path);
    }

    @Override
    public MatchResult findRoute(String method, String path) {
        LOG.debug("Searching for route match: {} {}", method, path);
        final String[] parts = (method + path).split("/");
        final Map<String, String> extractedVariables = new HashMap<>();
        RouteNode current = root;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (current.getStaticChildren().containsKey(part)) {
                current = current.getStaticChildren().get(part);
            } else if (current.getVariableChild() != null) {
                extractedVariables.put(current.getVariableName(), part);
                current = current.getVariableChild();
            } else {
                LOG.debug("Route not found (missed at node '{}'): {} {}", part, method, path);
                return null;
            }
        }
        if (current.getRoute() == null) {
            LOG.debug("Node exists but no route action assigned for: {} {}", method, path);
            return null;
        }
        LOG.debug("Route found for: {} {}, extracted variables: {}", method, path,
            extractedVariables);
        return new MatchResult(current.getRoute(), extractedVariables);
    }

    @Override
    public Set<String> getVariableNamesFor(String method, String path) {
        LOG.debug("Extracting variable names for validation: {} {}", method, path);
        final String[] parts = (method + path).split("/");
        final Set<String> variableNames = new HashSet<>();
        RouteNode current = root;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (part.startsWith(DELIMITER_START) && part.endsWith(DELIMITER_END)) {
                final String varName = part.substring(1, part.length() - 1);
                variableNames.add(varName);
                LOG.debug("Found variable placeholder '{}' in path", varName);
                current = current.getVariableChild();
            } else {
                current = current.getStaticChildren().get(part);
            }
            if (current == null) {
                LOG.debug("Traversal stopped: part '{}' does not exist in trie", part);
                break;
            }
        }
        LOG.debug("Extracted variable names for {} {}: {}", method, path, variableNames);
        return variableNames;
    }
}
