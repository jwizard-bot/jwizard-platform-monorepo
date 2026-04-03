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

import java.util.HashMap;
import java.util.Map;

class RouteNode {
    private final Map<String, RouteNode> children = new HashMap<>();
    private RouteNode variableChild = null;
    private String variableName = null;
    private Route route = null;

    Map<String, RouteNode> getStaticChildren() {
        return children;
    }

    RouteNode getVariableChild() {
        return variableChild;
    }

    void setVariableChild(RouteNode variableChild) {
        this.variableChild = variableChild;
    }

    String getVariableName() {
        return variableName;
    }

    void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    Route getRoute() {
        return route;
    }

    void setRoute(Route route) {
        this.route = route;
    }
}
