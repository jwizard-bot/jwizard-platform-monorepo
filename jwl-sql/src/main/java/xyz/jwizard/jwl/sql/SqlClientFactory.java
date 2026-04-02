package xyz.jwizard.jwl.sql;

import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

@FunctionalInterface
public interface SqlClientFactory {
    GenericSqlClient create(SqlDatabaseConfig config, ConnectionPoolFactory poolFactory);
}
