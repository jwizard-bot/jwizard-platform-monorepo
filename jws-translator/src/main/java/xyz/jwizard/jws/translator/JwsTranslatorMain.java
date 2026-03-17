package xyz.jwizard.jws.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.i18n.JwlI18n;

public class JwsTranslatorMain {
    private static final Logger LOG = LoggerFactory.getLogger(JwsTranslatorMain.class);

    public static void main(String[] args) {
        LOG.info("HELLO FROM JWS-TRANSLATOR");
        JwlI18n.hello();
    }
}
