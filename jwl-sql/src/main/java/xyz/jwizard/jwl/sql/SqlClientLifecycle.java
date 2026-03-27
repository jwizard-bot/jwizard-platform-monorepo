package xyz.jwizard.jwl.sql;

public interface SqlClientLifecycle {
    void start();

    void close();
}
