/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
