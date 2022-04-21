package org.harryng.demo.vertx.mutiny;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class AbstractVertxTest {
    static Logger logger = LoggerFactory.getLogger(AbstractVertxTest.class);
    protected Vertx vertx = null;

    @BeforeEach
    public void init() {
        vertx = Vertx.vertx();
    }

    @AfterEach
    public void destroy() {
        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));
    }
}
