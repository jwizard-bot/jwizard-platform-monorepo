package xyz.jwizard.jws.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.contracts.JwlContracts;
import xyz.jwizard.jwl.http.HttpServer;
import xyz.jwizard.jwl.persistence.JwlPersistence;

public class JwsApiMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsApiMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-API");
        JwlContracts.hello();
        JwlPersistence.hello();

        final HttpServer httpServer = HttpServer.builder()
            .basePackageToScan("xyz.jwizard.jws.api")
            .port(9091)
            .build();
        httpServer.start();
    }
}
