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
package xyz.jwizard.jwl.sql.config;

import java.util.Map;

import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.common.util.net.NetworkUtil;

public record SqlDatabaseConfig(
    SqlDatabaseDialect dialect,
    HostPort hostPort,
    String username,
    String password,
    String databaseName,
    int maxPoolSize
) {
    public static Builder builder() {
        return new Builder();
    }

    public String buildJdbcUrl() {
        return dialect.buildJdbcUrl(hostPort, databaseName);
    }

    public Map<String, String> getDriverProperties() {
        return dialect.defaultDriverProperties();
    }

    public static class Builder {
        private SqlDatabaseDialect dialect;
        private HostPort hostPort;
        private String username;
        private String password;
        private String databaseName;
        private int maxPoolSize = 10;

        private Builder() {
        }

        public Builder dialect(SqlDatabaseDialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder dialect(String dialectRaw) {
            dialect = SqlDatabaseDialect.fromString(dialectRaw);
            return this;
        }

        public Builder hostPort(HostPort hostPort) {
            this.hostPort = hostPort;
            return this;
        }

        public Builder address(String address) {
            this.hostPort = NetworkUtil.parseHostPort(address);
            return this;
        }

        public Builder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public SqlDatabaseConfig build() {
            Assert.notNull(dialect, "Database dialect cannot be null");
            Assert.notNull(hostPort, "Database host/port cannot be null");
            Assert.notNull(username, "Database username cannot be null");
            Assert.notNull(databaseName, "Database name cannot be null");
            return new SqlDatabaseConfig(
                dialect,
                hostPort,
                username,
                password,
                databaseName,
                maxPoolSize
            );
        }
    }
}
