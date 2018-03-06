package com.lumr.crawler.job;

import com.lumr.crawler.job.config.SpringVerticleFactory;
import com.lumr.crawler.job.verticle.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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

    @Autowired
    public void deployVerticles(SpringVerticleFactory factory, @Value("${vertx.springWorker.instances}") Integer instances, @Value("${vertx.worker.pool.size}") Integer workerSize) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(workerSize));
        vertx.registerVerticleFactory(factory);
        vertx.deployVerticle(factory.prefix() + ":" + MainVerticle.class.getName(), new DeploymentOptions().setInstances(instances));
    }
}
