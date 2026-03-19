package xyz.jwizard.jws.api;

import xyz.jwizard.jwl.common.bootstrap.AppBootstrapper;
import xyz.jwizard.jwl.common.bootstrap.DefaultBootstrapper;

@AppBootstrapper
public class JwsApiMain {
    public static void main(String[] args) {
        DefaultBootstrapper.run(JwsApiMain.class);
    }
}
