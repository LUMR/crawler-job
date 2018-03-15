package com.lumr.crawler.job.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 定时爬虫
 * Created by work on 2018/3/14.
 *
 * @author lumr
 */
@Component
public class TimerVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(TimerVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        /*vertx.deployVerticle("crawler-job:" + ClientVerticle.class.getName(), res -> {
            if (res.succeeded()) {
                startFuture.complete();
                send();
            }else
                startFuture.fail(res.cause());
        });*/
        start();
    }

    @Override
    public void start() throws Exception {
        vertx.executeBlocking(bk -> {
            send();
            bk.complete();
        }, rs -> LOG.info("请求发送完成"));
    }

    private void send() {
        EventBus eventBus = vertx.eventBus();

        JsonObject json = new JsonObject();
        json.put("host", "www.zhihu.com").put("port", 443).put("ssl", true);

        DeliveryOptions options = new DeliveryOptions().setSendTimeout(60000);

        for (int i = 19724800; i < 19725000; i++) {
            final int t = i;
            eventBus.send("com.web.get", json.copy().put("url", "/question/" + i), options, res -> {
                if (res.succeeded())
                    LOG.info("问题{}，get成功。", t);
                else
                    LOG.info("问题{}，get失败。{}", t, res.cause().getMessage());
            });
        }
    }
}
