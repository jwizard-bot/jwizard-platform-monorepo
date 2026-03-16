package xyz.jwizard.jws.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.JwlCommon;
import xyz.jwizard.jwl.contracts.JwlContracts;
import xyz.jwizard.jwl.persistence.JwlPersistence;
import xyz.jwizard.jwl.transport.JwlTransport;

public class JwsWorkerMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsWorkerMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-WORKER");
        JwlCommon.hello();
        JwlContracts.hello();
        JwlPersistence.hello();
        JwlTransport.hello();
    }
}
