package xyz.jwizard.jws.registry;

import xyz.jwizard.jwl.common.bootstrap.AppBootstrapper;
import xyz.jwizard.jwl.common.bootstrap.DefaultBootstrapper;

@AppBootstrapper
public class JwsRegistryMain {
    public static void main(String[] args) {
        DefaultBootstrapper.run(JwsRegistryMain.class);
    }
}
