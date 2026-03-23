package xyz.jwizard.jwl.queue.rabbitmq.connector;

public enum ConnectorType {
    SINGLE_NODE(new RabbitMqSingleNodeConnector()),
    CLUSTER(new RabbitMqClusterConnector()),
    ;

    private final RabbitMqConnector connector;

    ConnectorType(RabbitMqConnector connector) {
        this.connector = connector;
    }

    public RabbitMqConnector getConnector() {
        return connector;
    }
}
