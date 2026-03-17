package xyz.jwizard.jws.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.contracts.JwlContracts;

public class JwsGatewayMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsGatewayMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-GATEWAY");
        JwlContracts.hello();
    }
}
