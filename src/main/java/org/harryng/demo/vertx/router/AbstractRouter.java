package org.harryng.demo.vertx.router;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;

public abstract class AbstractRouter {
    static Logger logger = LoggerFactory.getLogger(AbstractRouter.class);
    private Router router = null;
    private Vertx vertx = null;

    public AbstractRouter(Vertx vertx, String path) {
        this.vertx = vertx;
        router = Router.router(vertx);
        init(path);
    }

    public Router getRouter() {
        return router;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void init(String path) {
        getRouter().route(path).handler(this::onRequest).failureHandler(this::onFailure);
        getRouter().errorHandler(404, this::onDefaultError);
    }

    public abstract void onRequest(RoutingContext context);

    public void onFailure(RoutingContext context) {

    }

    public void onDefaultError(RoutingContext context) {

    }
}
