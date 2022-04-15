package org.harryng.demo.vertx.config;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;

import java.util.HashMap;
import java.util.Map;

public class RouterConfig {

    static Map<String, Router> routerMap = new HashMap<>();

    public static Router createRootRouter(Vertx vertx){
        Router rootRouter = null;

        return rootRouter;
    }
}
