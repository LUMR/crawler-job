package com.lumr.crawler.job;

import com.lumr.crawler.job.config.SpringVerticleFactory;
import com.lumr.crawler.job.verticle.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Created by work on 2018/3/6.
 *
 * @author lumr
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

//    @Autowired
    private void deployVerticles(SpringVerticleFactory factory, @Value("${vertx.springWorker.instances}") Integer instances, @Value("${vertx.worker.pool.size}") Integer workerSize) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(workerSize));
        vertx.registerVerticleFactory(factory);
        vertx.deployVerticle(factory.prefix() + ":" + MainVerticle.class.getName(), new DeploymentOptions().setInstances(instances));
    }

    @EventListener
    @Autowired
    public void registeZookeeper(SpringVerticleFactory factory){
        ClusterManager manager = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(manager).setWorkerPoolSize(10);
        Vertx.clusteredVertx(options,res->{
            if (res.succeeded()){
                Vertx vertx = res.result();
                vertx.deployVerticle(factory.prefix()+":"+MainVerticle.class.getName(),new DeploymentOptions().setInstances(4));
            }else {
                System.exit(0);
            }
        });
    }
}
