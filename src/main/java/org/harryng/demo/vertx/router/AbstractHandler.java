package org.harryng.demo.vertx.router;

import io.vertx.mutiny.core.Vertx;

public class AbstractHandler {
    private Vertx vertx;

    public AbstractHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public Vertx getVertx() {
        return this.vertx;
    }
}
