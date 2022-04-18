package org.harryng.demo.vertx.router.http;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.harryng.demo.vertx.router.AbstractHandler;

import java.util.Map;

public class HttpIndexHandler extends AbstractHandler {
    public HttpIndexHandler(Vertx vertx) {
        super(vertx);
    }

    public void getIndex(RoutingContext context){
        var tEngine = TemplateHandler.getTemplateEngine(getVertx());
        tEngine.render(Map.of(), "templates/pages/html/index.html")
                .subscribe().with(buff -> {
                    context.response().end(buff).subscribe().with(it->{}, ex -> {});
                }, ex -> {});
    }
}
