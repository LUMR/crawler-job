package com.lumr.crawler.job.verticle;

import com.lumr.crawler.job.handler.ResponseResultHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
@Component
public class ClientVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(ClientVerticle.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        LOG.info("client部署完成。");
        startFuture.complete();
    }

    @Override
    public void start() throws Exception {
        WebClientOptions options = new WebClientOptions().setKeepAlive(true);
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("worker");
        WebClient client = WebClient.create(vertx, options);
        EventBus eventBus = vertx.eventBus();

        eventBus.<JsonObject>consumer("com.web.get", message -> {
            LOG.info("获得get任务：{}", message.body());
            JsonObject json = message.body();
            executor.executeBlocking(hr->{
                client.get(json.getInteger("port"),json.getString("host"), json.getString("url"))
                    .ssl(json.getBoolean("ssl", false))
                    .send(resp->{
                        if (resp.result().statusCode()!=200) {
                            hr.fail("地址无效");
                            return;
                        }
                        Buffer buffer = resp.result().body();
                        Document document = Jsoup.parse(buffer.toString());
                        LOG.info("标题:{},获得返回字节大小：{}",document.title(),buffer.length());
                        JsonObject resjson = json.copy()
                            .put("location",document.location())
                            .put("title",document.title());
                        redisTemplate.opsForList().rightPush("zhihu",resjson.toString());
                        hr.complete();
                    });
            },res->{
                if (res.succeeded())
                    message.reply("保存成功");
                else
                    message.fail(-1,"保存失败"+res.cause().toString());
            });

        }).completionHandler(ar -> {
            if (ar.succeeded())
                LOG.info("com.web.get 订阅完成");
            else
                LOG.error("com.web.get 订阅失败", ar.cause());
        });

        eventBus.<JsonObject>consumer("com.web.post", message -> {
            LOG.info("获取post任务：{}", message.body());
            JsonObject json = message.body();

            executor.executeBlocking(hr -> {
                client.post(json.getInteger("port"),json.getString("host"), json.getString("url"))
                    .ssl(json.getBoolean("ssl", false))
                    .send(new ResponseResultHandler(hr, redisTemplate));
            }, res -> {
                if (res.succeeded())
                    message.reply("保存成功");
                else
                    message.fail(-1, "保存失败"+res.cause().toString());
            });
        }).completionHandler(ar -> {
            if (ar.succeeded())
                LOG.info("com.web.post 订阅完成");
            else
                LOG.error("com.web.post 订阅失败", ar.cause());
        });

    }
}
