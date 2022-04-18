package org.harryng.demo.vertx.router.staticresource;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.handler.StaticHandler;
import org.harryng.demo.vertx.router.AbstractHandler;

public class StaticResourcesHandler extends AbstractHandler {

    private String path = "";

    public StaticResourcesHandler(Vertx vertx, String path) {
        super(vertx);
        this.path = path;
    }

    public StaticHandler createStaticHandler() {
        return StaticHandler.create(path);
    }
}
