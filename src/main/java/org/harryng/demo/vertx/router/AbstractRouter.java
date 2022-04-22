package org.harryng.demo.vertx.router;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;

public abstract class AbstractRouter {
    static Logger logger = LoggerFactory.getLogger(AbstractRouter.class);
    private Router router = null;
    private Vertx vertx = null;

    public AbstractRouter(Vertx vertx) {
        this.vertx = vertx;
    }

    public Router getRouter() {
        if(router == null){
            router = Router.router(vertx);
        }
        return router;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public AbstractRouter init(String path) {
        getRouter().route(path).handler(this::onRequest).failureHandler(this::onFailure);
        getRouter().allowForward(AllowForwardHeaders.ALL);
        getRouter().errorHandler(404, this::onDefaultError);
        initStaticRouting();
        initHttpWsRouting();
        return this;
    }

    protected abstract void initStaticRouting();
    protected abstract void initHttpWsRouting();
    public abstract void onRequest(RoutingContext context);
    public abstract void onFailure(RoutingContext context);
    public abstract void onDefaultError(RoutingContext context);
}
