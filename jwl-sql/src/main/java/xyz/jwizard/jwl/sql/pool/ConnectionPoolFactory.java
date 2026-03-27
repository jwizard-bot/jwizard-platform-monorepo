package xyz.jwizard.jwl.sql.pool;

import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;

public interface ConnectionPoolFactory {
    ManagedDataSource createPool(SqlDatabaseConfig config);
}
