package com.lumr.crawler.job.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;

/**
 * Created by work on 2018/3/7.
 *
 * @author lumr
 */
public class TestLauncher {
    public static void run(Class<? extends AbstractVerticle> clazz) {
        Launcher.main(new String[]{"run", clazz.getName()});
    }

}
