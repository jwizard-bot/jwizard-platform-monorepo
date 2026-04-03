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
package xyz.jwizard.jwl.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;
import xyz.jwizard.jwl.sql.pool.ManagedDataSource;

import javax.sql.DataSource;

public abstract class GenericSqlClient implements SqlClient, SqlClientLifecycle {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private final SqlDatabaseConfig config;
    private final ConnectionPoolFactory poolFactory;

    private DataSource dataSource;
    private Runnable closeAction;

    protected GenericSqlClient(SqlDatabaseConfig config, ConnectionPoolFactory poolFactory) {
        this.config = config;
        this.poolFactory = poolFactory;
    }

    @Override
    public final void start() {
        if (dataSource != null) {
            LOG.warn("Database client for '{}' is already started.", config.databaseName());
            return;
        }
        LOG.info("Starting database connection pool for: {}", config.databaseName());
        final ManagedDataSource pool = poolFactory.createPool(config);
        dataSource = pool.dataSource();
        closeAction = pool.closeAction();
    }

    @Override
    public final void close() {
        IoUtil.closeQuietly(closeAction);
    }

    protected DataSource getActiveDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Database client is not started, call start() first");
        }
        return dataSource;
    }
}
