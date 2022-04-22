package org.harryng.demo.vertx.router;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.common.template.TemplateEngine;
import org.harryng.demo.vertx.router.http.HttpUserHandler;
import org.harryng.demo.vertx.router.staticresource.StaticResourcesHandler;
import org.harryng.demo.vertx.router.ws.WsUserHandler;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RootRouter extends AbstractRouter {

    private Map<String, Map<HttpMethod, Consumer<RoutingContext>>> wsHttpRoutingMap = new HashMap<>();
    private Map<String, Consumer<RoutingContext>> wsRoutingMap = new HashMap<>();
    private TemplateEngine templateEngine = null;

    private HttpUserHandler httpUserHandler = new HttpUserHandler(getVertx());
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
        var staticHandler = staticResourcesHandler.createStaticHandler();
        var staticRouter = Router.router(getVertx());
        staticRouter.route("/static/*").handler(staticHandler)
                .failureHandler(this::onFailure);
        getRouter().mountSubRouter("/", staticRouter);
    }

    protected void declareWsRoutingMap() {
        wsRoutingMap.put("/user/add", wsUserHandler::add);
    }

    protected void declareHttpRoutingMap() {
        wsHttpRoutingMap.put("/user", Map.ofEntries(
                new AbstractMap.SimpleEntry<>(HttpMethod.GET, httpUserHandler::getById),
                new AbstractMap.SimpleEntry<>(HttpMethod.POST, httpUserHandler::add),
                new AbstractMap.SimpleEntry<>(HttpMethod.PUT, httpUserHandler::edit),
                new AbstractMap.SimpleEntry<>(HttpMethod.DELETE, httpUserHandler::remove),
                new AbstractMap.SimpleEntry<>(HttpMethod.PROPFIND, httpUserHandler::search))
        );
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
        wsHttpRoutingMap.forEach((key, key2Val) -> key2Val.forEach(
                (key2, val) -> httpRouter.route(key2, key).handler(val)));

        getRouter().mountSubRouter("/http", httpRouter);
        getRouter().mountSubRouter("/ws", wsRouter);
    }

    @Override
    public void onRequest(RoutingContext context) {
        logger.info("onRequest: path:[" + context.request().uri() + "] code:" + context.response().getStatusCode());
        context.reroute("/static/index.html");
    }

    @Override
    public void onFailure(RoutingContext context) {
        context.response()
                .end()
                .subscribe().with(itm -> {
                            logger.info("onFailure: path[" + context.request().uri() + "] code:"
                                    + context.response().getStatusCode());
                        },
                        ex -> logger.error("Route err:", ex));
    }

    @Override
    public void onDefaultError(RoutingContext context) {
        logger.info("onDefaultError: path[" + context.request().uri() + "] code:" + context.response().getStatusCode());
        context.reroute("/static/error.html");
//        context.response().set
    }
}
