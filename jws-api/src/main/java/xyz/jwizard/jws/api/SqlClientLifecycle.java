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
package xyz.jwizard.jws.api;

import java.util.List;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.config.SqlDatabaseDialect;
import xyz.jwizard.jwl.sql.jdbc.JdbcSqlClient;
import xyz.jwizard.jwl.sql.pool.hikaricp.HikariConnectionPoolFactory;
import xyz.jwizard.jwl.sql.registry.SqlDatabaseRegistry;

import jakarta.inject.Singleton;

@Singleton
class SqlClientLifecycle implements LifecycleHook {
    private final SqlDatabaseRegistry sqlDatabaseRegistry;

    SqlClientLifecycle() {
        sqlDatabaseRegistry = SqlDatabaseRegistry.builder()
            .poolFactory(HikariConnectionPoolFactory.create())
            .register(SqlDatabaseConfig.builder()
                .dialect(SqlDatabaseDialect.POSTGRESQL /*TODO: incoming from config server*/)
                .address("localhost:9115" /*TODO: incoming from config server*/)
                .credentials("postgres", "root" /*TODO: incoming from config server*/)
                .databaseName("jw_main" /*TODO: incoming from config server*/)
                .build(), JdbcSqlClient::new)
            .register(SqlDatabaseConfig.builder()
                .dialect(SqlDatabaseDialect.POSTGRESQL /*TODO: incoming from config server*/)
                .address("localhost:9115" /*TODO: incoming from config server*/)
                .credentials("postgres", "root" /*TODO: incoming from config server*/)
                .databaseName("jw_telemetry" /*TODO: incoming from config server*/)
                .build(), JdbcSqlClient::new)
            .build();
    }

    @Override
    public List<Class<? extends LifecycleHook>> dependsOn() {
        return List.of(KvServerLifecycle.class);
    }

    @Override
    public void onStart(ComponentProvider componentProvider, ClassScanner scanner) {
        sqlDatabaseRegistry.startAll();
    }

    @Override
    public void onStop() {
        sqlDatabaseRegistry.closeAll();
    }
}
