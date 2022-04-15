package org.harryng.demo.vertx.router.ws;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;

public class WsUserHandler extends AbstractWsHandler{

    public WsUserHandler(Vertx vertx) {
        super(vertx);
    }

    public void add(RoutingContext context){

    }
}
