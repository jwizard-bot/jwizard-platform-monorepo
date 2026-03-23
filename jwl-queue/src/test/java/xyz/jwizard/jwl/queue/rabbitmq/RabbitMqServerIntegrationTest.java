package xyz.jwizard.jwl.queue.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.TypeReference;
import xyz.jwizard.jwl.common.serialization.SerializerFormat;
import xyz.jwizard.jwl.common.serialization.SerializerRegistry;
import xyz.jwizard.jwl.common.serialization.json.JacksonSerializer;
import xyz.jwizard.jwl.common.serialization.raw.RawByteSerializer;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.common.util.net.HostPort;
import xyz.jwizard.jwl.queue.*;
import xyz.jwizard.jwl.queue.rabbitmq.connector.ConnectorType;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
public class RabbitMqServerIntegrationTest {
    @Container
    static final RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");

    private QueueServer server;
    private ComponentProvider componentProvider;
    private SerializerRegistry serializerRegistry;

    @BeforeEach
    void setUp() {
        componentProvider = mock(ComponentProvider.class);
        serializerRegistry = mock(SerializerRegistry.class);

        when(serializerRegistry.get(SerializerFormat.RAW))
            .thenReturn(RawByteSerializer.createDefault());
        when(serializerRegistry.get(SerializerFormat.JSON))
            .thenReturn(JacksonSerializer.createLenientForMessaging());
    }

    @AfterEach
    void tearDown() {
        IoUtil.closeQuietly(server);
    }

    @Test
    @DisplayName("should publish and receive raw byte message successfully")
    void shouldPublishAndReceiveMessage() throws InterruptedException {
        // given
        final HappyPathListener listener = new HappyPathListener();
        mockListenerRegistration(listener);
        startServer();
        // when
        final byte[] payload = "Hello RabbitMQ!".getBytes(StandardCharsets.UTF_8);
        server.publishToQueue("test.happy.queue", payload, SerializerFormat.RAW);

        // then
        final boolean received = listener.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(received).as("Message should be received").isTrue();
        final String receivedStr = new String(listener.getReceivedMessage(),
            StandardCharsets.UTF_8);
        assertThat(receivedStr).isEqualTo("Hello RabbitMQ!");
    }

    @Test
    @DisplayName("should route failed messages to Dead Letter Queue when DLX is enabled")
    void shouldRouteToDlxOnFailure() throws Exception {
        // given
        final FailingListener listener = new FailingListener();
        mockListenerRegistration(listener);
        startServer();
        // when
        final byte[] poisonPill = "Poison Pill".getBytes(StandardCharsets.UTF_8);
        server.publishToQueue("test.fail.queue", poisonPill, SerializerFormat.RAW);
        // then
        Thread.sleep(500);
        try (final Connection conn = createDirectConnection();
             Channel channel = conn.createChannel()) {

            final GetResponse response = channel.basicGet("test.fail.queue.dlq", true);
            assertThat(response).as("Message should be routed to DLQ").isNotNull();

            final String dlqMessage = new String(response.getBody(), StandardCharsets.UTF_8);
            assertThat(dlqMessage).isEqualTo("Poison Pill");
        }
    }

    @Test
    @DisplayName("should serialize, publish, receive and deserialize JSON object successfully")
    void shouldPublishAndReceiveJsonObject() throws InterruptedException {
        // given
        final JsonCommandListener listener = new JsonCommandListener();
        mockListenerRegistration(listener);
        startServer();
        final PlayTrackCommand command = new PlayTrackCommand("123456789",
            "https://youtube.com/watch?v=123");
        // when
        server.publishToQueue("test.json.queue", command);
        // then
        final boolean received = listener.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(received).as("JSON message should be received").isTrue();
        assertThat(listener.getReceivedCommand()).isNotNull();
        assertThat(listener.getReceivedCommand().guildId()).isEqualTo("123456789");
        assertThat(listener.getReceivedCommand().trackUrl())
            .isEqualTo("https://youtube.com/watch?v=123");
    }

    private void startServer() {
        server = RabbitMqServer.builder()
            .withConnector(ConnectorType.SINGLE_NODE)
            .nodes(Set.of(new HostPort(rabbitMQ.getHost(), rabbitMQ.getAmqpPort())))
            .username(rabbitMQ.getAdminUsername())
            .password(rabbitMQ.getAdminPassword())
            .virtualHost("/")
            .componentProvider(componentProvider)
            .serializerRegistry(serializerRegistry)
            .build();
        server.start();
    }

    private Connection createDirectConnection() throws Exception {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQ.getHost());
        factory.setPort(rabbitMQ.getAmqpPort());
        factory.setUsername(rabbitMQ.getAdminUsername());
        factory.setPassword(rabbitMQ.getAdminPassword());
        return factory.newConnection();
    }

    @SuppressWarnings("unchecked")
    private void mockListenerRegistration(QueueListener<?> listener) {
        when(componentProvider.getInstancesOf(any(TypeReference.class)))
            .thenReturn(Collections.singletonList(listener));
    }
}
