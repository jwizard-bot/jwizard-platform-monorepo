package xyz.jwizard.jws.gateway;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.serialization.json.JacksonSerializer;
import xyz.jwizard.jwl.common.serialization.raw.RawByteSerializer;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.queue.MessagePublisher;
import xyz.jwizard.jwl.queue.QueueServer;
import xyz.jwizard.jwl.queue.rabbitmq.RabbitMqServer;
import xyz.jwizard.jwl.queue.rabbitmq.connector.ConnectorType;

import java.util.Set;

@Singleton
class QueueServerLifecycle implements LifecycleHook {
    private final QueueServer queueServer;

    @Inject
    QueueServerLifecycle(ComponentProvider componentProvider) {
        queueServer = RabbitMqServer.builder()
            .rawNodes(Set.of("localhost:9191") /*TODO: incoming from config server*/)
            .withConnector(ConnectorType.SINGLE_NODE)
            .username("guest" /*TODO: incoming from config server*/)
            .password("guest" /*TODO: incoming from config server*/)
            .virtualHost("jwizard-main" /*TODO: incoming from config server*/)
            .serializerRegistry(new SerializerRegistry()
                .register(JacksonSerializer.createLenientForMessaging())
                .register(RawByteSerializer.createDefault())
            )
            .componentProvider(componentProvider)
            .build();
    }

    @Override
    public void onStart(ComponentProvider componentProvider) {
        queueServer.start();
    }

    @Override
    public void onStop() {
        IoUtil.closeQuietly(queueServer);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Produces
    MessagePublisher messagePublisher() {
        return queueServer;
    }
}
