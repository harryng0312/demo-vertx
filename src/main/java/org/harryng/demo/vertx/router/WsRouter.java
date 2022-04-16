package org.harryng.demo.vertx.router;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.StaticHandler;
import org.harryng.demo.vertx.router.ws.WsUserHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WsRouter extends AbstractRouter {

    private Map<String, Consumer<RoutingContext>> routingMap = null;

    private WsUserHandler wsUserHandler = null;

    public Map<String, Consumer<RoutingContext>> getRoutingMap() {
        if (routingMap == null) {
            routingMap = new HashMap<>();
        }
        return routingMap;
    }

    public WsUserHandler getWsUserHandler() {
        if (wsUserHandler == null) {
            wsUserHandler = new WsUserHandler(getVertx());
        }
        return wsUserHandler;
    }

    public WsRouter(Vertx vertx, String path) {
        super(vertx, path);
    }

    @Override
    public void init(String path) {
        super.init(path);
    }

    @Override
    protected void initStaticRouting() {
        var staticRouter = Router.router(getVertx());
        var staticHandler = StaticHandler.create("webapps");
        staticRouter.route("/static/*").handler(staticHandler)
                .failureHandler(this::onFailure);
        getRouter().mountSubRouter("/", staticRouter);
    }

    protected void declareRoutingMap(){
        getRoutingMap().put("/ws/user/add", getWsUserHandler()::add);
    }
    @Override
    protected void initRoutingMap() {
        declareRoutingMap();
        getRoutingMap().forEach((key, val) -> getRouter().route(key).handler(val));
    }

    @Override
    public void onRequest(RoutingContext context) {

    }

    @Override
    public void onFailure(RoutingContext context) {
        context.response()
                .setStatusCode(context.statusCode())
                .end()
                .subscribe().with(itm -> {
                            logger.info("OnFailure");
                        },
                        ex -> logger.error("Route err:", ex));
    }

    @Override
    public void onDefaultError(RoutingContext context) {
        context.reroute("/static/error.html");
    }
}
