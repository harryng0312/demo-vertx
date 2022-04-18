package org.harryng.demo.vertx.router.http;

import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.common.template.TemplateEngine;
import org.harryng.demo.vertx.router.AbstractHandler;

public class TemplateHandler extends AbstractHandler {
    private static TemplateEngine templateEngine = null;

    private TemplateHandler(Vertx vertx) {
        super(vertx);
    }

    protected static TemplateEngine createTemplateEngine(Vertx vertx, String path){

        return TemplateEngine.newInstance(ThymeleafTemplateEngine.create(vertx.getDelegate()));
    }

    public static TemplateEngine getTemplateEngine(Vertx vertx){
        if(templateEngine == null){
            templateEngine = createTemplateEngine(vertx,"templates");
        }
        return templateEngine;
    }
}
