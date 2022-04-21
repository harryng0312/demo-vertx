package org.harryng.demo.vertx.mutiny;

import io.vertx.core.net.NetClientOptions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class NetServerVerticleTest extends AbstractVertxTest{

    @Test
    public void testSendNetServerFromClient() {
        logger.info( "=====");
        var options = new NetClientOptions();
        var netClient = vertx.createNetClient(options);
        var run = netClient.connect(4321, "localhost").map(netSocket -> netSocket.handler(buffer -> {
            logger.info( "Client receive:" + new String(buffer.getBytes(), StandardCharsets.UTF_8));
        })).flatMap(netSocket -> {
            logger.info( "Client sent!");
            return netSocket.write("From client: hello server")
                    .flatMap(v -> netSocket.end())
                    .map(v -> netSocket);
//        }).subscribe().with(itm -> {});
        }).onFailure().transform(ex -> {
            logger.error("Ex:", ex);
            return ex;
        }).onItemOrFailure().transformToUni((netSocket, ex) -> netSocket.close());//.subscribe().with(itm->{});
//        vertx.executeBlockingAndAwait(run);
        vertx.executeBlockingAndAwait(run);
    }
}