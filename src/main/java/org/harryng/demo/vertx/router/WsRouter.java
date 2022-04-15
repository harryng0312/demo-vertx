package org.harryng.demo.vertx.router;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.harryng.demo.vertx.router.ws.WsUserHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WsRouter extends AbstractRouter {

    private Map<String, Consumer<RoutingContext>> routingMap = new HashMap<>();

    private WsUserHandler usUser = new WsUserHandler(getVertx());

    public WsRouter(Vertx vertx, String path) {
        super(vertx, path);
    }

    protected void initRoutingMap(){
        routingMap.put("/user/add", usUser::add);
    }

    @Override
    public void init(String path) {
        super.init(path);
        initRoutingMap();
    }

    @Override
    public void onRequest(RoutingContext context) {

    }
}
