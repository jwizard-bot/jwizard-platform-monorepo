package xyz.jwizard.jws.registry;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.json.JacksonSerializer;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.http.jetty.JettyHttpServer;

import java.util.Set;

@Singleton
public class HttpServerLifecycle implements LifecycleHook {
    private final HttpServer httpServer;

    @Inject
    HttpServerLifecycle(ComponentProvider componentProvider) {
        httpServer = JettyHttpServer.builder()
            .jsonSerializer(new JacksonSerializer())
            .componentProvider(componentProvider)
            .ignoredPaths(Set.of())
            .port(9092) /*TODO: incoming from config server*/
            .build();
    }

    // init last, destroy first
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onStart(ComponentProvider componentProvider) {
        httpServer.start();
    }

    @Override
    public void onStop() {
        IoUtil.closeQuietly(httpServer);
    }
}
