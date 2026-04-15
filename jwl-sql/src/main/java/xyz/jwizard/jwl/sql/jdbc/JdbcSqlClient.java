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
package xyz.jwizard.jwl.sql.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import xyz.jwizard.jwl.sql.GenericSqlClient;
import xyz.jwizard.jwl.sql.SqlDatabaseException;
import xyz.jwizard.jwl.sql.SqlRowMapper;
import xyz.jwizard.jwl.sql.config.SqlDatabaseConfig;
import xyz.jwizard.jwl.sql.pool.ConnectionPoolFactory;

public class JdbcSqlClient extends GenericSqlClient {
    public JdbcSqlClient(SqlDatabaseConfig config, ConnectionPoolFactory poolFactory) {
        super(config, poolFactory);
    }

    @Override
    public <T> List<T> query(String sql, SqlRowMapper<T> mapper, Object... params) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing SQL query: [{}] | Params: {}", sql, Arrays.toString(params));
        }
        try (final Connection conn = getActiveDataSource().getConnection();
             final PreparedStatement stmt = prepareStatement(conn, sql, params);
             final ResultSet rs = stmt.executeQuery()) {

            final List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            LOG.debug("Query returned {} row(s)", results.size());
            return results;
        } catch (SQLException e) {
            LOG.error("Failed to execute query: {}", sql, e);
            throw new SqlDatabaseException("SQL database query failed", e);
        }
    }

    @Override
    public <T> Optional<T> queryForObject(String sql, SqlRowMapper<T> mapper, Object... params) {
        final List<T> results = query(sql, mapper, params);
        if (results.isEmpty()) {
            LOG.debug("QueryForObject returned empty result for SQL: [{}]", sql);
            return Optional.empty();
        }
        if (results.size() > 1) {
            LOG.warn("Expected 1 result for query, but found {}. SQL: {}", results.size(), sql);
        }
        return Optional.of(results.getFirst());
    }

    @Override
    public int update(String sql, Object... params) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing SQL update: [{}] | Params: {}", sql, Arrays.toString(params));
        }
        try (final Connection conn = getActiveDataSource().getConnection();
             final PreparedStatement stmt = prepareStatement(conn, sql, params)) {
            final int affectedRows = stmt.executeUpdate();
            LOG.debug("Update affected {} row(s)", affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            LOG.error("Failed to execute update: {}", sql, e);
            throw new SqlDatabaseException("SQL database update failed", e);
        }
    }

    private PreparedStatement prepareStatement(Connection conn, String sql, Object... params)
        throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }
}
