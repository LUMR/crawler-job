package com.lumr.crawler.job.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
@Component
public class ZookeeperVerticle extends AbstractVerticle{
    private final Logger LOG = LoggerFactory.getLogger(ZookeeperVerticle.class);



    @Override
    public void start() throws Exception {
        ClusterManager manager = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(manager);
        Vertx.clusteredVertx(options,res->{
            if (res.succeeded()){
                Vertx vertx = res.result();
                LOG.info("zookeeper部署完成。");
            }else {
                LOG.warn("zookeeper 部署失败。");
            }
        });
    }
}
