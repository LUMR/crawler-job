package com.lumr.crawler.job;

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
@ComponentScan(basePackages = {"com.lumr.crawler.job.config", "com.lumr.crawler.job.verticle"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
