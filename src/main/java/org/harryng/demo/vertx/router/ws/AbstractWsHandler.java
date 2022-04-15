package org.harryng.demo.vertx.router.ws;

import io.vertx.mutiny.core.Vertx;

public class AbstractWsHandler {
    private Vertx vertx;

    public AbstractWsHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public Vertx getVertx() {
        return this.vertx;
    }
}
