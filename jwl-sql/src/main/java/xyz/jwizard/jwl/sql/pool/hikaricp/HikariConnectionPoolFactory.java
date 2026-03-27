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
