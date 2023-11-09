package org.harryng.demo.vertx.mutiny;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class TestThread extends AbstractVertxTest {
    @Test
    public void testVThread() throws InterruptedException {
        log.info("Main thread id: %s".formatted(Thread.currentThread().threadId()));
        final Thread vThread = Thread.startVirtualThread(() -> log.info("VThread id: %s".formatted(Thread.currentThread().threadId())));
        vThread.join();
    }

}
