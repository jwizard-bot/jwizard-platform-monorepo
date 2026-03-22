package xyz.jwizard.jwl.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.common.util.net.NetworkUtil;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class KvServer implements KeyValueStore, PubSubBroadcaster, Closeable {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final Set<HostPort> kvClusterNodes;
    protected final String password;

    protected KvServer(Set<HostPort> kvClusterNodes, String password) {
        this.kvClusterNodes = kvClusterNodes;
        this.password = password;
    }

    public void start() {
        if (kvClusterNodes.isEmpty()) {
            LOG.warn("Not providing any cluster nodes. Skipping configuration.");
            return;
        }
        LOG.info("KV server starting initializing with {} node(s).", kvClusterNodes.size());
        try {
            onStart();
        } catch (Exception ex) {
            throw new CriticalBootstrapException("KV server connection failed", ex);
        }
    }

    protected abstract void onStart();

    public abstract static class Builder<B extends Builder<B, T>, T extends KvServer> {
        protected Set<HostPort> nodes = new HashSet<>();
        protected String password;

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B nodes(Set<HostPort> nodes) {
            this.nodes = nodes;
            return self();
        }

        // as host:port
        public B rawNodes(Set<String> rawNodes) {
            this.nodes = rawNodes.stream()
                .map(NetworkUtil::parseHostPort)
                .map(hp -> new HostPort(hp.host(), hp.port()))
                .collect(Collectors.toSet());
            return self();
        }

        public B password(String password) {
            this.password = password;
            return self();
        }

        public abstract T build();
    }
}
