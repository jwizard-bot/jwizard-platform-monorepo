package xyz.jwizard.jwl.sql.pool;

import javax.sql.DataSource;

public record ManagedDataSource(
    DataSource dataSource,
    Runnable closeAction
) {
}
