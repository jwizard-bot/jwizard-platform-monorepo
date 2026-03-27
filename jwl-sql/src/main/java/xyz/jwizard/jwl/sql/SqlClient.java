package xyz.jwizard.jwl.sql;

import java.util.List;
import java.util.Optional;

public interface SqlClient {
    <T> List<T> query(String sql, SqlRowMapper<T> mapper, Object... params);

    <T> Optional<T> queryForObject(String sql, SqlRowMapper<T> mapper, Object... params);

    int update(String sql, Object... params);
}
