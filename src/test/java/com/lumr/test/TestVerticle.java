package com.lumr.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

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
        Integer question = 31427896;
        client.get(443, "www.zhihu.com", "/question/"+question).ssl(true).send(res -> {
            if (res.succeeded()) {
                Buffer buffer = res.result().body();
                System.out.println("返回数据大小："+buffer.length());
                File file = new File("/Users/work/zhihu/"+question+".html");

                try(OutputStream out = new FileOutputStream(file)) {
                    buffer.getByteBuf().readBytes(out,buffer.length());
                    /*for (int i = 0,leng = buffer.length(); i < leng; i+=1000) {
                        buffer.getByteBuf().readBytes(bytes,i,1000);
                        out.write(bytes);
                    }*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(res.cause().getMessage());
            }
            vertx.close();
        });

    }
}
