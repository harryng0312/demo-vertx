package org.harryng.demo.vertx.router.ws;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;

public class WsUserHandler extends AbstractWsHandler{

    static Logger logger = LoggerFactory.getLogger(WsUserHandler.class);

    public WsUserHandler(Vertx vertx) {
        super(vertx);
    }

    public void add(RoutingContext context){
        logger.info("User add request");
        context.request().toWebSocket().map(serverWebSocket ->
                        serverWebSocket.handler(buffer -> {
                            logger.info("Client call:");
                            var obj = new JsonObject(new String(buffer.getBytes(), StandardCharsets.UTF_8));
                            var params = obj.getJsonArray("params", new JsonArray());
                            // start trans scope
                            // end trans scope
                            serverWebSocket.writeTextMessage(obj.toString()).subscribe()
                                    .with(v->{}, ex->{});
                        }).drainHandler(() -> {
                        }).closeHandler(() -> {
                        }).endHandler(() -> {
                            logger.info("Client disconnected!");
                        }).exceptionHandler(ex -> serverWebSocket.writeTextMessage(ex.getMessage())
                                .subscribe().with(v -> {
                                }, ex1 -> {
                                })
                        )
        ).subscribe().with(serverWebSocket -> logger.info("Client connected!"), ex -> logger.error("Socket ex:", ex));
    }
}
