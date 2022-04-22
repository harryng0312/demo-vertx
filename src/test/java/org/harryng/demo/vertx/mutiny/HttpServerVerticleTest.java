package org.harryng.demo.vertx.mutiny;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.http.HttpClientRequest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpServerVerticleTest extends AbstractVertxTest {

    @Test
    public void testGetHttpServerFromClient() {
        var options = new HttpClientOptions();
        var client = vertx.createHttpClient(options);
        var reqOptions = new RequestOptions()
                .setHost("localhost").setPort(8080)
                .addHeader("content-type", "application/json");
        var req = client
                .request(reqOptions.setURI("/hello/1?name=test01").setMethod(HttpMethod.GET))
                .map(httpClientRequest -> {
                    logger.info("Connecting...");
                    return httpClientRequest;
                }).flatMap(HttpClientRequest::connect)
                .map(httpClientResponse -> httpClientResponse.bodyHandler(buffer -> {
                    logger.info( "Receive:" + new String(buffer.getBytes(), StandardCharsets.UTF_8));
                }))
                .onFailure().invoke(ex -> logger.error("Ex:", ex))
                .onItemOrFailure().invoke((itm, ex) -> client.closeAndForget());
        vertx.executeBlockingAndAwait(req);
    }

    @Test
    public void testPostHttpServerFromClient() {
        var options = new HttpClientOptions();
        var client = vertx.createHttpClient(options);
        var reqOptions = new RequestOptions()
                .setHost("localhost").setPort(8080)
                .addHeader("content-type", "application/json");
        var reqData = "{\n" +
                "    \"id\":1,\n" +
                "    \"name\":\"test sub 1\"\n" +
                "}";
        var req = client
                .request(reqOptions.setURI("/hello/1?name=test01").setMethod(HttpMethod.POST))
                .map(httpClientRequest -> {
                    logger.info( "Pushing data...");
                    return httpClientRequest;
                }).flatMap(httpClientRequest -> httpClientRequest.send(reqData))
                .map(httpClientResponse -> httpClientResponse.bodyHandler(buffer -> {
                    logger.info( "Receive body:" + new String(buffer.getBytes(), StandardCharsets.UTF_8));
//                })).map(httpClientResponse -> httpClientResponse.handler(buffer -> {
//                    logger.info( "Receive:" + new String(buffer.getBytes(), StandardCharsets.UTF_8));
                }))
                .onFailure().invoke(ex -> logger.error("Ex:", ex))
                .onItemOrFailure().invoke((itm, ex) -> client.closeAndForget());
        vertx.executeBlockingAndAwait(req);
    }

    @Test
    public void testThreadPool(){
        var executor= Executors.newFixedThreadPool(1);
        executor.execute(()->{
            logger.info("in thread 1:" + Thread.currentThread().hashCode());
        });
        executor.execute(()->{
            logger.info("in thread 2:" + Thread.currentThread().hashCode());
        });
        executor.shutdown();

        var index = new AtomicInteger(1);
        var thread = new Thread(() -> {
            var currThread = Thread.currentThread();
            logger.info("in plain thread " + index.getAndIncrement() + ": " + Thread.currentThread().hashCode());

            currThread.interrupt();
            if(index.get()<=2){
                currThread.run();
            }
        });
        thread.start();
    }
}
