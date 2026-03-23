package xyz.jwizard.jwl.queue.rabbitmq.connector;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import xyz.jwizard.jwl.common.util.net.HostPort;

import java.util.Set;

public class RabbitMqSingleNodeConnector implements RabbitMqConnector {
    @Override
    public Connection connect(Set<HostPort> nodes, ConnectionFactory baseFactory)
        throws Exception {
        final HostPort singleNode = nodes.iterator().next();
        baseFactory.setHost(singleNode.host());
        baseFactory.setPort(singleNode.port());
        return baseFactory.newConnection();
    }

    @Override
    public ConnectorType type() {
        return ConnectorType.SINGLE_NODE;
    }
}
