package com.lumr.crawler.job.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PostResultHandler implements Handler<AsyncResult<HttpResponse<Buffer>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostResultHandler.class);

    @Override
    public void handle(AsyncResult<HttpResponse<Buffer>> resp) {
        Buffer buffer = resp.result().body();
        Document document = Jsoup.parse(buffer.toString());
        LOGGER.info(document.title());


        LOGGER.info("获得返回字节大小：{}", buffer.length());
        Jsoup.parse(buffer.toString());
    }
}
