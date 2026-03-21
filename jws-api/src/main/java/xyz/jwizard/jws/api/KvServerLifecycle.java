package xyz.jwizard.jws.api;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.kv.KeyValueStore;
import xyz.jwizard.jwl.kv.KvServer;
import xyz.jwizard.jwl.kv.PubSubBroadcaster;
import xyz.jwizard.jwl.kv.jedis.JedisServer;
import xyz.jwizard.jwl.kv.jedis.factory.FactoryType;

import java.util.Set;

@Singleton
class KvServerLifecycle implements LifecycleHook {
    private final KvServer kvServer;

    KvServerLifecycle() {
        kvServer = JedisServer.builder()
            .rawNodes(Set.of("127.0.0.1:9193" /*TODO: getting from config server*/))
            .password(null /*TODO: getting from config server*/)
            .withFactory(FactoryType.SINGLE_NODE)
            .build();
    }

    @Override
    public void onStart(ComponentProvider provider) {
        kvServer.start();
    }

    @Override
    public void onStop() {
        IoUtil.closeQuietly(kvServer);
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Produces
    KeyValueStore keyValueStore() {
        return kvServer;
    }

    @Produces
    PubSubBroadcaster pubSubBroadcaster() {
        return kvServer;
    }
}
