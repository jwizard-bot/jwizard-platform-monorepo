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
package xyz.jwizard.jwl.graph.neo4j;

import xyz.jwizard.jwl.graph.GraphProtocol;

public enum Neo4jGraphProtocol implements GraphProtocol {
    BOLT("bolt", false, false),
    NEO4J("neo4j", false, false),
    NEO4J_S("neo4j+s", true, true),  // TLS with trusted CA
    NEO4J_SSC("neo4j+ssc", true, false), // self-signed CA
    ;

    private final String scheme;
    private final boolean encrypted;
    private final boolean strictTlsValidation;

    Neo4jGraphProtocol(String scheme, boolean encrypted, boolean strictTlsValidation) {
        this.scheme = scheme;
        this.encrypted = encrypted;
        this.strictTlsValidation = strictTlsValidation;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean isEncrypted() {
        return encrypted;
    }

    @Override
    public boolean requestStrictTlsValidation() {
        return strictTlsValidation;
    }
}
