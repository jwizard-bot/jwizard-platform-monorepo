package xyz.jwizard.jwl.sql.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.sql.GenericSqlClient;
import xyz.jwizard.jwl.sql.SqlClient;
import xyz.jwizard.jwl.sql.SqlClientLifecycle;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.jdbc.JdbcSqlClient;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlDatabaseRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SqlDatabaseRegistry.class);

    private final Map<String, GenericSqlClient> clients = new ConcurrentHashMap<>();
    private final ConnectionPoolFactory poolFactory;

    private SqlDatabaseRegistry(Builder builder) {
        poolFactory = builder.poolFactory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void register(SqlDatabaseConfig config) {
        final String dbName = config.databaseName();
        if (clients.containsKey(dbName)) {
            throw new IllegalArgumentException("Database '" + dbName + "' is already registered!");
        }
        LOG.debug("Registering SQL client for database: '{}'", dbName);
        final JdbcSqlClient sqlClient = new JdbcSqlClient(config, poolFactory);
        clients.put(dbName, sqlClient);
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
        LOG.info("Started {} SQL clients", clients.size());
    }

    public void closeAll() {
        for (final SqlClientLifecycle client : clients.values()) {
            client.close();
        }
        clients.clear();
        LOG.info("All SQL clients has been stopped");
    }

    public static class Builder {
        private final List<SqlDatabaseConfig> configs = new ArrayList<>();
        private ConnectionPoolFactory poolFactory;

        private Builder() {
        }

        public Builder poolFactory(ConnectionPoolFactory poolFactory) {
            this.poolFactory = poolFactory;
            return this;
        }

        public Builder register(SqlDatabaseConfig config) {
            this.configs.add(config);
            return this;
        }

        public SqlDatabaseRegistry build() {
            Assert.notNull(poolFactory, "PoolFactory cannot be null");
            final SqlDatabaseRegistry registry = new SqlDatabaseRegistry(this);
            for (final SqlDatabaseConfig config : configs) {
                registry.register(config);
            }
            LOG.info("Registered {} database definition(s), not yet started", configs.size());
            return registry;
        }
    }
}
