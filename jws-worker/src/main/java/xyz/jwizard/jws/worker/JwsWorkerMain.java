package xyz.jwizard.jws.worker;

import xyz.jwizard.jwl.common.bootstrap.AppBootstrapper;
import xyz.jwizard.jwl.common.bootstrap.DefaultBootstrapper;

@AppBootstrapper
public class JwsWorkerMain {
    public static void main(String[] args) {
        DefaultBootstrapper.run(JwsWorkerMain.class);
    }
}
