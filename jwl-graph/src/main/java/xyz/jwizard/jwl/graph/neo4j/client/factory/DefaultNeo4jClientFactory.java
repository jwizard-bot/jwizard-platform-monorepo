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
package xyz.jwizard.jwl.graph.neo4j.client.factory;

import java.net.URI;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.graph.client.GraphClient;
import xyz.jwizard.jwl.graph.client.factory.GraphClientFactory;
import xyz.jwizard.jwl.graph.neo4j.client.Neo4jClient;
import xyz.jwizard.jwl.net.NetworkUtil;

public class DefaultNeo4jClientFactory implements GraphClientFactory<Neo4jConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultNeo4jClientFactory.class);

    private DefaultNeo4jClientFactory() {
    }

    public static DefaultNeo4jClientFactory create() {
        return new DefaultNeo4jClientFactory();
    }

    @Override
    public GraphClient createAndInitClient(Neo4jConfig config) {
        final URI uri = NetworkUtil.parseToUri(config.getProtocol(), config.getAddress());
        LOG.debug("Initializing Neo4j driver for: {}", uri);

        final Config.ConfigBuilder configBuilder = Config.builder();
        if (config.getProtocol().isEncrypted()) {
            LOG.trace("Neo4j encryption enabled (strict: {})",
                config.getProtocol().requestStrictTlsValidation());
            configBuilder.withEncryption();
            if (config.getProtocol().requestStrictTlsValidation()) {
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustSystemCertificates());
            } else {
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustAllCertificates());
            }
        } else {
            LOG.trace("Neo4j encryption disabled");
            configBuilder.withoutEncryption();
        }
        final Driver driver = GraphDatabase.driver(
            NetworkUtil.parseToUri(config.getProtocol(), config.getAddress()),
            AuthTokens.basic(config.getUsername(), config.getPassword()),
            configBuilder.build()
        );
        LOG.debug("Verifying connectivity to Neo4j");
        try {
            driver.verifyConnectivity();
            LOG.info("Successfully connected to Neo4j at {}", uri);
        } catch (Exception ex) {
            driver.close();
            throw ex;
        }
        return new Neo4jClient(driver);
    }
}
