package xyz.jwizard.jwl.common.util.io;

@FunctionalInterface
public interface CloseAction<T> {
    void perform(T resource) throws Exception;
}
