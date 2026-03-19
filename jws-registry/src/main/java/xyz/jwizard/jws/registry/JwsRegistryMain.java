package xyz.jwizard.jws.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.contracts.JwlContracts;
import xyz.jwizard.jwl.http.HttpServer;

public class JwsRegistryMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsRegistryMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-REGISTRY");
        JwlContracts.hello();

        final HttpServer httpServer = HttpServer.builder()
            .basePackageToScan("xyz.jwizard.jws.registry")
            .port(9092)
            .build();
        httpServer.start();
    }
}
