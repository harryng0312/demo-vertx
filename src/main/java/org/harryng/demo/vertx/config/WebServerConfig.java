package org.harryng.demo.vertx.config;

import io.vertx.core.http.HttpServerOptions;

public class WebServerConfig {

    public static int getPort(){
        return 8080;
    }
    public static HttpServerOptions getHttpServerOptions(){
        return new HttpServerOptions();
    }
}
