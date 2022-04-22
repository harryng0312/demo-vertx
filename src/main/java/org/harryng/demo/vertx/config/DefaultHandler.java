package org.harryng.demo.vertx.config;

import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.http.HttpServerRequest;

import java.util.function.Consumer;

public class DefaultHandler {
    static Logger logger = LoggerFactory.getLogger(DefaultHandler.class);

    public static Consumer<HttpServerRequest> getInvalidRequestHandler() {
        return HttpServerRequest::bodyAndForget;
    }

    public static Consumer<Throwable> getExceptionHandler() {
        return Unchecked.consumer(ex -> {
//            logger.error("Default ex:", ex);
            throw new Exception("Default ex:", ex);
        });
    }
}
