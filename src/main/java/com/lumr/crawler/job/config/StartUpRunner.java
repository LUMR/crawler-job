package com.lumr.crawler.job.config;

import com.lumr.crawler.job.verticle.ClientVerticle;
import com.lumr.crawler.job.verticle.MainVerticle;
import com.lumr.crawler.job.verticle.TimerVerticle;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by work on 2018/3/8.
 *
 * @author lumr
 */
@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private SpringVerticleFactory factory;
    @Value("${vertx.run.host}")
    private String host;
    @Value("${vertx.run.port}")
    private int port;
    @Value("${vertx.worker.pool.size}")
    private int workerSize;
    @Value("${vertx.springWorker.instances}")
    private int instances;

    @Override
    public void run(String... args) throws Exception {

        final String mode = System.getProperty("run.mode");

        ClusterManager manager = new ZookeeperClusterManager();
        //ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setWorkerPoolSize(workerSize).setClusterManager(manager).setClusterHost(host).setClusterPort(port);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.registerVerticleFactory(factory);
                DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(instances);

                String deployName;
                if ("web".equals(mode))
                    deployName = factory.prefix() + ":" + TimerVerticle.class.getName();
                else
                    deployName = factory.prefix() + ":" + ClientVerticle.class.getName();

                vertx.deployVerticle(deployName, deploymentOptions, re -> {
                    if (re.succeeded())
                        LOG.info("部署完成，当前服务地址：{},端口号：{}", options.getClusterHost(), options.getClusterPort());
                    else {
                        LOG.error("部署失败", re.cause());
                        System.exit(0);
                    }
                });

            } else {
                LOG.error("zookeeper注册失败。", res.cause());
                System.exit(0);
            }
        });
    }
}
