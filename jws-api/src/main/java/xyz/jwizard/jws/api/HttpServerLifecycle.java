package xyz.jwizard.jws.api;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.serialization.json.JacksonSerializer;
import xyz.jwizard.jwl.common.util.io.IoUtil;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.http.jetty.JettyHttpServer;

import java.util.List;
import java.util.Set;

@Singleton
class HttpServerLifecycle implements LifecycleHook {
    private final HttpServer httpServer;

    @Inject
    HttpServerLifecycle(ComponentProvider componentProvider) {
        httpServer = JettyHttpServer.builder()
            .componentProvider(componentProvider)
            .jsonSerializer(JacksonSerializer.createDefaultStrictMapper())
            .ignoredPaths(Set.of())
            .port(9091) /*TODO: incoming from config server*/
            .build();
    }

    @Override
    public List<Class<? extends LifecycleHook>> dependsOn() {
        return List.of(
            KvServerLifecycle.class,
            SqlClientLifecycle.class
        );
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
