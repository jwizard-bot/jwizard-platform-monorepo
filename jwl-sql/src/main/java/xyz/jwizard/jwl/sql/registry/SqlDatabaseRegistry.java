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
package xyz.jwizard.jwl.sql.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.sql.GenericSqlClient;
import xyz.jwizard.jwl.sql.SqlClient;
import xyz.jwizard.jwl.sql.SqlClientFactory;
import xyz.jwizard.jwl.sql.SqlClientLifecycle;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlDatabaseRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SqlDatabaseRegistry.class);

    private final Map<String, GenericSqlClient> clients = new ConcurrentHashMap<>();

    private SqlDatabaseRegistry(Map<String, GenericSqlClient> builtClients) {
        clients.putAll(builtClients);
    }

    public static Builder builder() {
        return new Builder();
    }

    // return simple SqlClient for hide lifecycle methods
    public SqlClient get(String databaseName) {
        LOG.debug("Fetching SQL client for database: '{}'", databaseName);
        final SqlClient client = clients.get(databaseName);
        if (client == null) {
            throw new IllegalStateException("No SQL client registered for database: " +
                databaseName);
        }
        return client;
    }

    public void startAll() {
        for (final SqlClientLifecycle client : clients.values()) {
            client.start();
        }
        LOG.info("Started {} SQL client(s)", clients.size());
    }

    public void closeAll() {
        for (final SqlClientLifecycle client : clients.values()) {
            client.close();
        }
        clients.clear();
    }

    public static class Builder {
        private final List<Runnable> registrationTasks = new ArrayList<>();
        private final Map<String, GenericSqlClient> finalClients = new ConcurrentHashMap<>();
        private ConnectionPoolFactory poolFactory;

        private Builder() {
        }

        public Builder poolFactory(ConnectionPoolFactory poolFactory) {
            this.poolFactory = poolFactory;
            return this;
        }

        public Builder register(SqlDatabaseConfig config, SqlClientFactory factory) {
            registrationTasks.add(() -> {
                final String dbName = config.databaseName();
                if (finalClients.containsKey(dbName)) {
                    throw new IllegalArgumentException("Database '" + dbName +
                        "' is already registered");
                }
                LOG.debug("Building SQL client for database: '{}'", dbName);
                final GenericSqlClient sqlClient = factory.create(config, this.poolFactory);
                finalClients.put(dbName, sqlClient);
            });
            return this;
        }

        public SqlDatabaseRegistry build() {
            Assert.notNull(poolFactory, "PoolFactory cannot be null");
            for (final Runnable task : registrationTasks) {
                task.run();
            }
            LOG.info("Registered and built {} database definition(s), not yet started",
                finalClients.size());
            return new SqlDatabaseRegistry(finalClients);
        }
    }
}
