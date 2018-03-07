package com.lumr.crawler.job.verticle;

import com.lumr.crawler.job.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
public class ClientVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(ClientVerticle.class);

    public static void main(String[] args) {
        Runner.runExample(ClientVerticle.class);
//        TestLauncher.run(ClientVerticle.class);
    }

    @Override
    public void start() throws Exception {
        WebClientOptions options = new WebClientOptions().setKeepAlive(false);
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("worker");
        WebClient client = WebClient.create(vertx, options);

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("com.web.talk", message -> {
            LOG.info("获得对话消息：{}", message.body());
        });

        eventBus.consumer("com.web.pub", message -> {
            LOG.info("获取订阅消息：{}", message.body());
            executor.executeBlocking(hr -> {
                client.get(443, "lagou.com", "/zhaopin/Java").ssl(true).send(ar -> {
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
