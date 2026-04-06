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
package xyz.jwizard.jwl.graph.neo4j.client;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.MapAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.graph.GraphDatabaseException;
import xyz.jwizard.jwl.graph.client.GraphClient;

import java.util.List;
import java.util.Map;

public class Neo4jClient implements GraphClient {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jClient.class);

    private final Driver driver;

    public Neo4jClient(Driver driver) {
        this.driver = driver;
    }

    @Override
    public List<Map<String, Object>> read(String query, Map<String, Object> parameters) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing read query: {} with params: {}", query.replace("\n", " "),
                parameters);
        }
        try (final Session session = driver.session()) {
            return session.executeRead(tx -> tx.run(query, parameters).list(MapAccessor::asMap));
        } catch (Exception ex) {
            throw new GraphDatabaseException("Neo4j read transaction failed", ex);
        }
    }

    @Override
    public List<Map<String, Object>> write(String query, Map<String, Object> parameters) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing write query with return: {} with params: {}",
                query.replace("\n", " "), parameters);
        }
        try (final Session session = driver.session()) {
            return session.executeWrite(tx -> tx.run(query, parameters).list(MapAccessor::asMap));
        } catch (Exception ex) {
            throw new GraphDatabaseException("Neo4j write transaction failed", ex);
        }
    }

    @Override
    public void execute(String query, Map<String, Object> parameters) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing write query: {} with params: {}", query.replace("\n", " "),
                parameters);
        }
        try (final Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(query, parameters).consume();
                return null;
            });
        } catch (Exception ex) {
            throw new GraphDatabaseException("Neo4j execution failed", ex);
        }
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(driver, Driver::close);
    }
}
