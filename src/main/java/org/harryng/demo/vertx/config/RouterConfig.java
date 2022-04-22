package org.harryng.demo.vertx.config;

import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.common.template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class RouterConfig {

    static TemplateEngine templateEngine = null;
//    static Map<String, Router> routerMap = new HashMap<>();

    public static Router createRootRouter(Vertx vertx){
        Router rootRouter = null;

        return rootRouter;
    }

    public static TemplateEngine getTemplateEngine(Vertx vertx){
        if(templateEngine == null){
            templateEngine = TemplateEngine.newInstance(ThymeleafTemplateEngine.create(vertx.getDelegate()));
        }
        return templateEngine;
    }
}
