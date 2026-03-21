package xyz.jwizard.jwl.kv.jedis.factory;

public enum FactoryType {
    SINGLE_NODE(new SingleNodeJedisClientFactory()),
    CLUSTER(new ClusterJedisClientFactory()),
    ;

    private final JedisClientFactory factory;

    FactoryType(JedisClientFactory factory) {
        this.factory = factory;
    }

    public JedisClientFactory getFactory() {
        return factory;
    }
}
