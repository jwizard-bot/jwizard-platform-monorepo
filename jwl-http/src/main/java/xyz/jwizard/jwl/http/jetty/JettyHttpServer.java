package xyz.jwizard.jwl.http.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.common.util.Assert;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.http.HttpRequestHandler;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.http.jetty.adapter.JettyHttpRequestHandlerAdapter;

import java.util.concurrent.Executors;

public class JettyHttpServer extends HttpServer {
    private Server server;
    private ServerConnector connector;

    protected JettyHttpServer(AbstractBuilder<?> builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void start() {
        final HttpRequestHandler httpRequestHandler = prepareRequestHandler();

        final QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
        queuedThreadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
        queuedThreadPool.setName("jwl-vt-pool");

        server = new Server(queuedThreadPool);
        connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        server.setHandler(new JettyHttpRequestHandlerAdapter(httpRequestHandler));
        try {
            server.start();
        } catch (Exception ex) {
            throw new CriticalBootstrapException("HTTP server startup failed", ex);
        }
    }

    @Override
    public int getLocalPort() {
        Assert.state(connector != null && connector.isRunning(), "Connector is not running!");
        return connector.getLocalPort();
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(server, AbstractLifeCycle::stop);
    }

    public static class Builder extends AbstractBuilder<Builder> {
        private Builder() {
        }

        @Override
        public HttpServer build() {
            validate();
            return new JettyHttpServer(this);
        }
    }
}
