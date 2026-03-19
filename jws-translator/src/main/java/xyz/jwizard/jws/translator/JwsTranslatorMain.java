package xyz.jwizard.jws.translator;

import xyz.jwizard.jwl.common.bootstrap.AppBootstrapper;
import xyz.jwizard.jwl.common.bootstrap.DefaultBootstrapper;

@AppBootstrapper
public class JwsTranslatorMain {
    public static void main(String[] args) {
        DefaultBootstrapper.run(JwsTranslatorMain.class);
    }
}
