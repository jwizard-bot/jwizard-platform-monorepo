package xyz.jwizard.jws.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.contracts.JwlContracts;
import xyz.jwizard.jwl.persistence.JwlPersistence;

public class JwsWorkerMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsWorkerMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-WORKER");
        JwlContracts.hello();
        JwlPersistence.hello();
    }
}
