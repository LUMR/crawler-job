package com.lumr.crawler.job.verticle;

import com.lumr.crawler.job.handler.RespContentTypeHandlerImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        vertx.deployVerticle("crawler-job:" + ClientVerticle.class.getName(), res -> {
            if (res.succeeded()) {
                LOG.info("web部署完成。");
                startFuture.complete();
            } else
                startFuture.fail(res.cause());
        });
    }

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().handler(new RespContentTypeHandlerImpl());

        JsonObject json = new JsonObject();
        json.put("host", "www.zhihu.com").put("port", 443).put("ssl", true);

        router.post("/getWebPage").handler(ctx -> {
            String index = ctx.request().getParam("index");

            for (int i = Integer.valueOf(index), end = i + 500; i < end; i++) {
                eventBus.send("com.web.get", json.copy().put("url", "/question/" + index), reply -> {
                    if (reply.succeeded()) {
                        JsonObject result = (JsonObject) reply.result().body();
                        ctx.response().end(result.toString());
                    } else {
                        ctx.response().end(reply.cause().getMessage());
                    }
                });
            }
        });

        router.get("/getWebPage").handler(ctx -> {
            String index = ctx.request().getParam("index");
            ctx.response().setChunked(true);
            for (int i = Integer.valueOf(index), end = i + 500; i < end; i++) {
                eventBus.send("com.web.get", json.copy().put("url", "/question/" + i), reply -> {
                    if (reply.succeeded()) {
                        String result = (String) reply.result().body();
                        ctx.response().write(result);
                    } else {
                        ctx.response().write(reply.cause().getMessage());
                    }
                });
            }
        });

        router.post("/postWebPage").handler(ctx -> {
            eventBus.send("com.web.post", json, reply -> {
                if (reply.succeeded()) {
                    JsonObject result = (JsonObject) reply.result().body();
                    ctx.response().end(result.toString());
                } else {
                    ctx.response().end(reply.cause().getMessage());
                }
            });
        });

        router.get("/").handler(ctx -> {
            resp(ctx).end("hello");
            LOG.info("get /");
        });

        router.get("/test").handler(ctx -> {
            String msg = ctx.request().getParam("msg");
            eventBus.send("com.web.talk", msg);
            resp(ctx).end("你好");
            LOG.info("get /test");
        });

        router.get("/we").handler(ctx -> {
            eventBus.publish("com.web.pub", "publish");
            ctx.response().setChunked(true);
            resp(ctx).write("你好，").end("我的AA等等");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        LOG.info("HTTP server started on port 8080");
    }

    private HttpServerResponse resp(RoutingContext context) {
        return context.response();
    }
}
