package com.lumr.crawler.job.config;

import com.lumr.crawler.job.Application;
import com.lumr.crawler.job.verticle.ClientVerticle;
import com.lumr.crawler.job.verticle.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by work on 2018/3/8.
 *
 * @author lumr
 */
@Component
public class StartUpRunner implements CommandLineRunner{

    private static final Logger LOG = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private SpringVerticleFactory factory;
    @Value("${run.host}")
    private String host;

    @Override
    public void run(String... args) throws Exception {

        final String mode = System.getProperty("run.mode");

        ClusterManager manager = new ZookeeperClusterManager();
//        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setWorkerPoolSize(10).setClusterManager(manager).setClusterHost(host);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.registerVerticleFactory(factory);
                DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(4);
                if ("web".equals(mode))
                    vertx.deployVerticle(factory.prefix() + ":" + MainVerticle.class.getName(), deploymentOptions);
                else
                    vertx.deployVerticle(factory.prefix() + ":" + ClientVerticle.class.getName(), deploymentOptions);

                LOG.info("注册完成，当前服务地址：{},公共地址：{}",options.getClusterHost(),options.getClusterPublicHost());
            } else {
                LOG.error("启动失败", res.cause());
                System.exit(0);
            }
        });
    }
}
