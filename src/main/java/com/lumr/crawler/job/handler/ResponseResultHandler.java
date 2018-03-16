package com.lumr.crawler.job.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;


public class ResponseResultHandler implements Handler<AsyncResult<HttpResponse<Buffer>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseResultHandler.class);

    private final Future<Object> hr;

    private final StringRedisTemplate redisTemplate;

    private final JsonObject JSON;

    public ResponseResultHandler(Future<Object> hr, StringRedisTemplate redisTemplate, JsonObject json) {
        this.hr = hr;
        this.redisTemplate = redisTemplate;
        this.JSON = json;
    }

    @Override
    public void handle(AsyncResult<HttpResponse<Buffer>> resp) {
        if (resp.result().statusCode()!=200) {
            hr.fail("地址无效");
            return;
        }
        Buffer buffer = resp.result().body();
        Document document = Jsoup.parse(buffer.toString());
        LOGGER.info("标题:{},获得返回字节大小：{}",document.title(),buffer.length());
        JSON.put("location",document.location()).put("title",document.title());
        redisTemplate.opsForList().rightPush("zhihu",JSON.toString());
        hr.complete();
    }
}
