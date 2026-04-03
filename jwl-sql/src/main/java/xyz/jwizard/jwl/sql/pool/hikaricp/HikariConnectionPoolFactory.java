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
package xyz.jwizard.jwl.sql.pool.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;
import xyz.jwizard.jwl.sql.pool.ManagedDataSource;

public class HikariConnectionPoolFactory implements ConnectionPoolFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HikariConnectionPoolFactory.class);

    private HikariConnectionPoolFactory() {
    }

    public static ConnectionPoolFactory create() {
        return new HikariConnectionPoolFactory();
    }

    @Override
    public ManagedDataSource createPool(SqlDatabaseConfig config) {
        final String jdbcUrl = config.buildJdbcUrl();
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());

        hikariConfig.setMaximumPoolSize(config.maxPoolSize());
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(5000);
        hikariConfig.setIdleTimeout(600000); // 10 minutes
        hikariConfig.setMaxLifetime(1800000); // 30 minutes

        config.getDriverProperties().forEach(hikariConfig::addDataSourceProperty);

        final HikariDataSource pool = new HikariDataSource(hikariConfig);
        LOG.info("Created HikariCP pool for URL: {}", jdbcUrl);

        return new ManagedDataSource(pool, pool::close);
    }
}
