package xyz.jwizard.jws.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.JwlCommon;
import xyz.jwizard.jwl.i18n.JwlI18n;
import xyz.jwizard.jwl.transport.JwlTransport;

public class JwsTranslatorMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsTranslatorMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-TRANSLATOR");
        JwlCommon.hello();
        JwlI18n.hello();
        JwlTransport.hello();
    }
}
