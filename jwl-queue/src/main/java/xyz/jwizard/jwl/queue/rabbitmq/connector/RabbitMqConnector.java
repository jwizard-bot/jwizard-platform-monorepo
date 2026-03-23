package xyz.jwizard.jwl.queue.rabbitmq.connector;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import xyz.jwizard.jwl.common.util.net.HostPort;

import java.util.Set;

public interface RabbitMqConnector {
    Connection connect(Set<HostPort> nodes, ConnectionFactory baseFactory) throws Exception;

    ConnectorType type();
}
