package xyz.jwizard.jws.api;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.config.SqlDatabaseDialect;
import xyz.jwizard.jwl.sql.pool.hikaricp.HikariConnectionPoolFactory;
import xyz.jwizard.jwl.sql.registry.SqlDatabaseRegistry;

@Singleton
class SqlClientLifecycle implements LifecycleHook {
    private final SqlDatabaseRegistry sqlDatabaseRegistry;

    SqlClientLifecycle() {
        sqlDatabaseRegistry = SqlDatabaseRegistry.builder()
            .poolFactory(HikariConnectionPoolFactory.create())
            .register(SqlDatabaseConfig.builder()
                .dialect(SqlDatabaseDialect.POSTGRESQL /*TODO: incoming from config server*/)
                .address("localhost:9195" /*TODO: incoming from config server*/)
                .credentials("postgres", "root" /*TODO: incoming from config server*/)
                .databaseName("jw_main" /*TODO: incoming from config server*/)
                .build())
            .register(SqlDatabaseConfig.builder()
                .dialect(SqlDatabaseDialect.POSTGRESQL /*TODO: incoming from config server*/)
                .address("localhost:9195" /*TODO: incoming from config server*/)
                .credentials("postgres", "root" /*TODO: incoming from config server*/)
                .databaseName("jw_telemetry" /*TODO: incoming from config server*/)
                .build())
            .build();
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public void onStart(ComponentProvider componentProvider) {
        sqlDatabaseRegistry.startAll();
    }

    @Override
    public void onStop() {
        sqlDatabaseRegistry.closeAll();
    }
}
