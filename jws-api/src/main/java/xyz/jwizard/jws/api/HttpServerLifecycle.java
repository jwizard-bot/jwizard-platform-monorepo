package xyz.jwizard.jws.api;

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.json.JacksonSerializer;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.http.jetty.JettyHttpServer;

@Singleton
public class HttpServerLifecycle implements LifecycleHook {
    private HttpServer httpServer;

    // init last, destroy first
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onStart(ComponentProvider provider) {
        httpServer = JettyHttpServer.builder()
            .componentProvider(provider)
            .jsonSerializer(new JacksonSerializer())
            .port(9091) /*TODO: incoming from config server*/
            .build();
        httpServer.start();
    }

    @Override
    public void onStop() {
        IoUtil.closeQuietly(httpServer);
    }
}
