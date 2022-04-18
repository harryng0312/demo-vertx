package org.harryng.demo.vertx.router.http;

import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.common.template.TemplateEngine;
import org.harryng.demo.vertx.router.AbstractHandler;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;
import java.util.Set;

public class TemplateHandler extends AbstractHandler {
    private static TemplateEngine templateEngine = null;

    private TemplateHandler(Vertx vertx) {
        super(vertx);
    }

    protected static void initTemplateResolver(TemplateEngine templateEngine) {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateEngine.<org.thymeleaf.TemplateEngine>unwrap().setTemplateResolver(templateResolver);
    }

    protected static void initMessageResolver(TemplateEngine templateEngine) {
//        var customMessageResolver = new CustomMessageResolver();
        var msgResolver = new StandardMessageResolver();
//        msgResolver.
        templateEngine.<org.thymeleaf.TemplateEngine>unwrap().setMessageResolver(msgResolver);
    }

    protected static TemplateEngine createTemplateEngine(Vertx vertx) {
        return TemplateEngine.newInstance(ThymeleafTemplateEngine.create(vertx.getDelegate()));
    }

    public static TemplateEngine getTemplateEngine(Vertx vertx) {
        if (templateEngine == null) {
            templateEngine = createTemplateEngine(vertx);
            initTemplateResolver(templateEngine);
            initMessageResolver(templateEngine);
        }
        return templateEngine;
    }
}
