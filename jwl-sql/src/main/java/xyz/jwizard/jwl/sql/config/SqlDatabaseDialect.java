package xyz.jwizard.jwl.sql.config;

import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.util.net.HostPort;

import java.util.Map;

public enum SqlDatabaseDialect {
    POSTGRESQL("jdbc:postgresql://%s:%d/%s", Map.of(
        "cachePrepStmts", "true",
        "prepStmtCacheSize", "250",
        "prepStmtCacheSqlLimit", "2048",
        "reWriteBatchedInserts", "true"
    )),
    ;

    private final String urlTemplate;
    private final Map<String, String> defaultDriverProperties;

    SqlDatabaseDialect(String urlTemplate, Map<String, String> defaultDriverProperties) {
        this.urlTemplate = urlTemplate;
        this.defaultDriverProperties = defaultDriverProperties;
    }

    public static SqlDatabaseDialect fromString(String dialectName) {
        try {
            return SqlDatabaseDialect.valueOf(dialectName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CriticalBootstrapException("Unsupported database dialect: "
                + dialectName, ex);
        }
    }

    public String buildJdbcUrl(HostPort hostPort, String databaseName) {
        return String.format(urlTemplate, hostPort.host(), hostPort.port(), databaseName);
    }

    public Map<String, String> defaultDriverProperties() {
        return defaultDriverProperties;
    }
}
