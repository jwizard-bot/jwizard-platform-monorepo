package xyz.jwizard.jws.gateway;

import xyz.jwizard.jwl.common.bootstrap.AppBootstrapper;
import xyz.jwizard.jwl.common.bootstrap.DefaultBootstrapper;

@AppBootstrapper
public class JwsGatewayMain {
    public static void main(String[] args) {
        DefaultBootstrapper.run(JwsGatewayMain.class);
    }
}
