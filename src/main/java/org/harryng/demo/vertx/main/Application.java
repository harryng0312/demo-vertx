package org.harryng.demo.vertx.main;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import org.harryng.demo.vertx.config.WebServerVertical;

public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
        var vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setWorker(false)
                .setInstances(1);
        logger.info("Deplopment starting...");
        vertx.deployVerticleAndAwait(WebServerVertical::new, options);
        logger.info("Deplopment completed");
    }
}
