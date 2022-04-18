package org.harryng.demo.vertx.router.http;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.harryng.demo.vertx.router.AbstractHandler;

import java.util.Map;

public class HttpAuthHandler extends AbstractHandler {
    public HttpAuthHandler(Vertx vertx) {
        super(vertx);
    }

    public void getLogin(RoutingContext context){
        var tEngine = TemplateHandler.getTemplateEngine(getVertx());
        tEngine.render(Map.of(), "pages/auth/login")
                .subscribe().with(buff -> {
                    context.response().end(buff).subscribe().with(it->{}, ex -> {});
                }, ex -> {});
    }

    public void postLogin(RoutingContext context){
        var tEngine = TemplateHandler.getTemplateEngine(getVertx());
        tEngine.render(Map.of(), "pages/auth/login")
                .subscribe().with(buff -> {
                    context.response().end(buff).subscribe().with(it->{}, ex -> {});
                }, ex -> {});
    }
}
