package org.harryng.demo.vertx.router;

import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.harryng.demo.vertx.router.http.HttpAuthHandler;
import org.harryng.demo.vertx.router.http.HttpIndexHandler;
import org.harryng.demo.vertx.router.http.HttpUserHandler;
import org.harryng.demo.vertx.router.staticresource.StaticResourcesHandler;
import org.harryng.demo.vertx.router.ws.WsUserHandler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class RootRouter extends AbstractRouter {

    private Map<String, Map<Consumer<RoutingContext>, List<HttpMethod>>> wsHttpRoutingMap = new HashMap<>();
    private Map<String, Consumer<RoutingContext>> wsRoutingMap = new HashMap<>();
    private HttpUserHandler httpUserHandler = new HttpUserHandler(getVertx());
    private HttpIndexHandler httpIndexHandler = new HttpIndexHandler(getVertx());
    private HttpAuthHandler httpAuthHandler = new HttpAuthHandler(getVertx());
    private WsUserHandler wsUserHandler = new WsUserHandler(getVertx());

    public RootRouter(Vertx vertx) {
        super(vertx);
    }

    @Override
    public RootRouter init(String path) {
        super.init(path);
        return this;
    }

    @Override
    protected void initStaticRouting() {
        var staticResourcesHandler = new StaticResourcesHandler(getVertx(), "webapps");
        var staticRouter = Router.router(getVertx());
        var staticHandler = staticResourcesHandler.createStaticHandler();
        staticRouter.route("/*").handler(staticHandler)
                .failureHandler(this::onFailure);
        getRouter().mountSubRouter("/static/", staticRouter);
    }

    protected void declareWsRoutingMap() {
        wsRoutingMap.put("/user/add", wsUserHandler::add);
    }

    protected void declareHttpRoutingMap() {
        wsHttpRoutingMap.put("/index", Map.ofEntries(
                new AbstractMap.SimpleEntry<>(httpIndexHandler::getIndex, List.of())));
        wsHttpRoutingMap.put("/login", Map.ofEntries(
                new AbstractMap.SimpleEntry<>(httpAuthHandler::getLogin, List.of(HttpMethod.GET)),
                new AbstractMap.SimpleEntry<>(httpAuthHandler::postLogin, List.of(HttpMethod.POST))
        ));
        wsHttpRoutingMap.put("/user", Map.ofEntries(
                new AbstractMap.SimpleEntry<>(httpUserHandler::getById, List.of(HttpMethod.GET)),
                new AbstractMap.SimpleEntry<>(httpUserHandler::add, List.of(HttpMethod.POST)),
                new AbstractMap.SimpleEntry<>(httpUserHandler::edit, List.of(HttpMethod.PUT)),
                new AbstractMap.SimpleEntry<>(httpUserHandler::remove, List.of(HttpMethod.DELETE)),
                new AbstractMap.SimpleEntry<>(httpUserHandler::search, List.of(HttpMethod.PROPFIND))));
    }

    @Override
    protected void initHttpWsRouting() {
        declareHttpRoutingMap();
        declareWsRoutingMap();

        var wsRouter = Router.router(getVertx());
        wsRouter.route("/").failureHandler(this::onFailure);
        wsRoutingMap.forEach((key, val) -> wsRouter.route(key).handler(val));

        var httpRouter = Router.router(getVertx());
        httpRouter.route("/").failureHandler(this::onFailure);
        wsHttpRoutingMap.forEach((path, func) -> func.forEach(
                (cons, method) -> {
                    if (method.isEmpty()) {
                        httpRouter.route(path).handler(cons);
                    } else {
                        method.forEach(meth -> httpRouter.route(meth, path).handler(cons));
                    }
                }));

        getRouter().mountSubRouter("/http", httpRouter);
        getRouter().mountSubRouter("/ws", wsRouter);
    }

    @Override
    public void onRequest(RoutingContext context) {
        context.reroute("/http/index");
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
