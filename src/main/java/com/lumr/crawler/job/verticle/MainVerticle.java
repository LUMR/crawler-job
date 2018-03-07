package com.lumr.crawler.job.verticle;

import com.lumr.crawler.job.handler.RespContentTypeHandlerImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
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

        router.route().handler(BodyHandler.create());
        router.route().handler(new RespContentTypeHandlerImpl());

        router.get("/").handler(ctx -> {
            resp(ctx).end("hello");
            LOG.info("get /");
        });

        router.get("/test").handler(ctx -> {
            resp(ctx).end("你好");
            LOG.info("get /test");
        });

        router.get("/we").handler(ctx -> resp(ctx).write("你好，").end("我的AA等等"));

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        LOG.info("HTTP server started on port 8080");
    }

    private HttpServerResponse resp(RoutingContext context) {
        return context.response();
    }
}
