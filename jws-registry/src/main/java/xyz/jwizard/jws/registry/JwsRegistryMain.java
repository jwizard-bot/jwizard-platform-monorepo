package xyz.jwizard.jws.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.contracts.JwlContracts;

public class JwsRegistryMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsRegistryMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-REGISTRY");
        JwlContracts.hello();
    }
}
