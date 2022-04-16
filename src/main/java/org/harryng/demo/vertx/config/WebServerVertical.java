package org.harryng.demo.vertx.config;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.harryng.demo.vertx.router.RootRouter;

public class WebServerVertical extends AbstractVerticle {

    static Logger logger = LoggerFactory.getLogger(WebServerVertical.class);

    protected Uni<Void> init(){
        var server = vertx.createHttpServer(WebServerConfig.getHttpServerOptions());
        var rootRouter = new RootRouter(vertx).init("/").getRouter();
        return server.requestHandler(rootRouter)
                .exceptionHandler(DefaultHandler.getExceptionHandler())
                .invalidRequestHandler(DefaultHandler.getInvalidRequestHandler())
                .listen(WebServerConfig.getPort())
                .invoke(() -> logger.info("HTTP server started on port " + server.actualPort()))
                .replaceWithVoid();
    }

    protected Uni<Void> destroy(){
        return vertx.close();
    }

    @Override
    public Uni<Void> asyncStart() {
        super.asyncStart();
        return init();
    }

    @Override
    public Uni<Void> asyncStop() {
        return destroy();
    }
}
