package org.harryng.demo.vertx.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;

public class MainConfig {
    protected void init(Vertx vertx) {
        var store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject()
                        .put("path", "my-config.yaml"));

        var retriever = ConfigRetriever.create(vertx.getDelegate(),
                new ConfigRetrieverOptions().addStore(store));
        retriever.configStream().handler(jsonObj -> {

        });
    }
}
