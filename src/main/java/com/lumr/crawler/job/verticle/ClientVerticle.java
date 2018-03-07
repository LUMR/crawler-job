package com.lumr.crawler.job.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
@Component
public class ClientVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(ClientVerticle.class);

    @Override
    public void start() throws Exception {
        WebClientOptions options = new WebClientOptions().setKeepAlive(true);
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("worker");
        WebClient client = WebClient.create(vertx, options);

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("com.web.get", message -> {
            LOG.info("获得get任务：{}", message.body());
        });

        eventBus.consumer("com.web.post", message -> {
            LOG.info("获取post任务：{}", message.body());
            JsonObject json = (JsonObject) message.body();

            executor.executeBlocking(hr -> {
                client.get( json.getString("host"), json.getString("url"))
                    .ssl(json.getBoolean("ssl", false))
                    .send(ar -> {
                        HttpResponse<Buffer> response = ar.result();
                        LOG.info("获得返回字节大小：{}", response.body().length());
                        Buffer buffer = response.body();

                        File file = new File("/Users/work/Documents/test.html");
                        try (OutputStream out = new FileOutputStream(file)) {
                            byte[] bytes = new byte[1000];
                            for (int s = 0, e = 999; s < buffer.length(); s += 1000, e += 1000) {
                                buffer.getBytes(s, e, bytes, 0);
                                out.write(bytes);
                            }
                            hr.complete();
                        } catch (IOException e) {
                            hr.fail(e);
                        }
                    });
            }, res -> {
                if (res.succeeded())
                    message.reply("保存成功");
                else
                    message.fail(-1, "保存失败，io错误");
            });

        });

    }
}
