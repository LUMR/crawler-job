package com.lumr.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by work on 2018/3/9.
 *
 * @author lumr
 */
public class TestVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", TestVerticle.class.getName()});
    }

    @Override
    public void start() throws Exception {
        WebClient client = WebClient.create(vertx);
        //31427895  67158058
        client.get(443, "www.zhihu.com", "/question/31427895").ssl(true).send(res -> {
            if (res.succeeded()) {
                Buffer buffer = res.result().body();
                try (OutputStream out = new FileOutputStream("/Users/work/test1.html")) {
                    buffer.getByteBuf().readBytes(out, buffer.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(buffer.length());

            } else {
                System.out.println(res.cause().getMessage());
            }
        });

        client.get(443,"www.zhihu.com","/api/v4/questions/31427895/similar-questions?include=data%5B*%5D.answer_count%2Cauthor%2Cfollower_count&limit=5")
            .ssl(true).send(res->{
                if (res.succeeded()){
                    Buffer buffer = res.result().body();
                    try (OutputStream out = new FileOutputStream("/Users/work/test2.html")) {
                        buffer.getByteBuf().readBytes(out, buffer.length());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(buffer.length());
                }
        });
    }
}
