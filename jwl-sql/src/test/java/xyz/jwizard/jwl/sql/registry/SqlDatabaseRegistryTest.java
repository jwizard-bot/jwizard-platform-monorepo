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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.jwizard.jwl.sql.SqlClient;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.config.SqlDatabaseDialect;
import xyz.jwizard.jwl.sql.jdbc.JdbcSqlClient;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

@ExtendWith(MockitoExtension.class)
class SqlDatabaseRegistryTest {
    @Mock
    private ConnectionPoolFactory mockPoolFactory;

    @Test
    @DisplayName("should register and retrieve SQL client successfully")
    void shouldRegisterAndRetrieveClient() {
        // given
        final SqlDatabaseConfig config = SqlDatabaseConfig.builder()
            .dialect(SqlDatabaseDialect.POSTGRESQL)
            .address("localhost:5432")
            .credentials("user", "pass")
            .databaseName("test_db")
            .build();
        final SqlDatabaseRegistry registry = SqlDatabaseRegistry.builder()
            .poolFactory(mockPoolFactory)
            .register(config, JdbcSqlClient::new)
            .build();
        // when
        final SqlClient client = registry.get("test_db");
        // then
        assertNotNull(client);
    }

    @Test
    @DisplayName("should throw exception when registering duplicate database name")
    void shouldThrowExceptionWhenRegisteringDuplicateDatabase() {
        // given
        final SqlDatabaseConfig config = SqlDatabaseConfig.builder()
            .dialect(SqlDatabaseDialect.POSTGRESQL)
            .address("localhost:5432")
            .credentials("user", "pass")
            .databaseName("duplicate_db")
            .build();
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> SqlDatabaseRegistry.builder()
                .poolFactory(mockPoolFactory)
                .register(config, JdbcSqlClient::new)
                .register(config, JdbcSqlClient::new)
                .build());
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("should throw exception when getting non-existent database client")
    void shouldThrowExceptionWhenGettingNonExistentDatabase() {
        // given
        final SqlDatabaseRegistry registry = SqlDatabaseRegistry.builder()
            .poolFactory(mockPoolFactory)
            .build();
        // when & then
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> registry.get("ghost_db"));
        assertTrue(exception.getMessage().contains("No SQL client registered"));
    }
}
