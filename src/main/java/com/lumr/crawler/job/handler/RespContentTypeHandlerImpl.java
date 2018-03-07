package com.lumr.crawler.job.handler;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
public class RespContentTypeHandlerImpl implements ResponseContentTypeHandler {
    @Override
    public void handle(RoutingContext rc) {
        rc.addHeadersEndHandler(v -> {
            MultiMap headers = rc.response().headers();
            if (headers.contains(CONTENT_TYPE)) {
                return;
            }
            headers.add(CONTENT_TYPE, "text/html;charset=UTF-8");
        });
        rc.next();
    }
}
