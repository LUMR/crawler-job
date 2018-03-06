package com.lumr.crawler.job.config;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by work on 2018/3/6.
 *
 * @author lumr
 */
@Component
public class SpringVerticleFactory implements VerticleFactory,ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "crawler-job";
    }

    @Override
    public boolean blockingCreate() {
        return true;
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        String clazz = VerticleFactory.removePrefix(verticleName);
        return (Verticle)applicationContext.getBean(Class.forName(clazz));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
