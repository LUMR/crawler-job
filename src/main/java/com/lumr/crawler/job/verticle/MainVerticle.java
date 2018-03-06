package com.lumr.crawler.job.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.impl.BodyHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(new BodyHandlerImpl());

        router.get("/").handler(ctx -> {
            ctx.response().putHeader("contentType", "application/json").end("hello");
            LOG.info("get /");
        });

        router.get("/test").handler(ctx -> {
            ctx.response().putHeader("contentType", "application/json").end("你好");
            LOG.info("get /test");
        });

        router.get("/we").handler(ctx->{
            ctx.response().end("我的AA等等");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        LOG.info("HTTP server started on port 8080");
    }
}
