package xyz.jwizard.jwl.common.bootstrap.lifecycle;

import java.util.Collection;
import java.util.List;

public interface LifecycleGraph {
    void addNode(LifecycleHook hook);

    void addNodes(Collection<? extends LifecycleHook> hooks);

    List<LifecycleHook> resolve();
}
