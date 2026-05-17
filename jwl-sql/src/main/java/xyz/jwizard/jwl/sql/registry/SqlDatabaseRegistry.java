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

import java.util.ArrayList;
import java.util.List;

import xyz.jwizard.jwl.common.registry.GenericConcurrentRegistry;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.sql.GenericSqlClient;
import xyz.jwizard.jwl.sql.SqlClient;
import xyz.jwizard.jwl.sql.SqlClientFactory;
import xyz.jwizard.jwl.sql.SqlClientLifecycle;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

public class SqlDatabaseRegistry extends GenericConcurrentRegistry<String, GenericSqlClient> {
    private SqlDatabaseRegistry(List<SqlRegistryConfig> sqlRegistryConfigs,
                                ConnectionPoolFactory poolFactory) {
        super();
        registerFromConfigs(sqlRegistryConfigs, poolFactory);
    }

    public static Builder builder() {
        return new Builder();
    }

    // return simple SqlClient for hide lifecycle methods
    public SqlClient getClient(String databaseName) {
        return super.get(databaseName);
    }

    public void startAll() {
        for (final SqlClientLifecycle client : super.getAll()) {
            client.start();
        }
        log.info("Started {} SQL client(s)", super.getEntries().size());
    }

    public void closeAll() {
        for (final SqlClientLifecycle client : super.getAll()) {
            client.close();
        }
        log.info("Closed {} SQL client(s)", super.getEntries().size());
        super.clear();
    }

    private void registerFromConfigs(List<SqlRegistryConfig> sqlRegistryConfigs,
                                     ConnectionPoolFactory poolFactory) {
        for (final SqlRegistryConfig config : sqlRegistryConfigs) {
            final String dbName = config.config().databaseName();
            log.debug("Building SQL client for database: '{}'", dbName);
            final GenericSqlClient sqlClient = config.factory()
                .create(config.config(), poolFactory);
            register(dbName, sqlClient);
        }
        log.info("Registered and built {} database definition(s), not yet started",
            getAll().size());
    }

    public static class Builder {
        private final List<SqlRegistryConfig> sqlRegistryConfigs = new ArrayList<>();
        private ConnectionPoolFactory poolFactory;

        private Builder() {
        }

        public Builder poolFactory(ConnectionPoolFactory poolFactory) {
            this.poolFactory = poolFactory;
            return this;
        }

        public Builder register(SqlDatabaseConfig config, SqlClientFactory factory) {
            sqlRegistryConfigs.add(new SqlRegistryConfig(config, factory));
            return this;
        }

        public SqlDatabaseRegistry build() {
            Assert.notNull(poolFactory, "PoolFactory cannot be null");
            return new SqlDatabaseRegistry(sqlRegistryConfigs, poolFactory);
        }
    }
}
