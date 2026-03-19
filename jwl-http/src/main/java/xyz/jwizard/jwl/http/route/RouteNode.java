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
