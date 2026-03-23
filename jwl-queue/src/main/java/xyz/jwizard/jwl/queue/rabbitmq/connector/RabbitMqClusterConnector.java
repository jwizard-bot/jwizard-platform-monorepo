package xyz.jwizard.jwl.queue.rabbitmq.connector;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import xyz.jwizard.jwl.common.util.net.HostPort;

import java.util.Set;

public class RabbitMqClusterConnector implements RabbitMqConnector {
    @Override
    public Connection connect(Set<HostPort> nodes, ConnectionFactory baseFactory)
        throws Exception {
        final Address[] addresses = nodes.stream()
            .map(node -> new Address(node.host(), node.port()))
            .toArray(Address[]::new);
        return baseFactory.newConnection(addresses);
    }

    @Override
    public ConnectorType type() {
        return ConnectorType.CLUSTER;
    }
}
