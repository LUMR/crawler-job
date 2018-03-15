package com.lumr.test;

import io.netty.util.internal.MathUtil;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by work on 2018/3/9.
 *
 * @author lumr
 */
public class Test {
    public static void main(String[] args) {
        double pi=0;
        double dx=1e-3;
        for(double x=-100;x<=+100;x+=dx){
            pi+=Math.exp(-x*x)*dx;
        }
        System.out.println(pi*pi);

        BigDecimal pi1 = new Test().pi();

        System.out.printf("%s,%d",pi1,pi1.toString().length());
        File file = new File("/Users/work/num.txt");
        try(FileOutputStream out = new FileOutputStream(file)){
            out.write(pi1.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BigDecimal pi(){
        BigDecimal pi = BigDecimal.valueOf(2);
        BigDecimal t = pi;
        BigDecimal n = BigDecimal.ONE;
        BigDecimal m = BigDecimal.valueOf(3);
        while (t.compareTo(BigDecimal.ZERO)>0) {
            t = t.multiply(n).divide(m,100000, RoundingMode.HALF_EVEN);
            pi=t.add(pi);
            n = n.add(BigDecimal.ONE);
            m = m.add(BigDecimal.valueOf(2));
        }

        return pi;
    }
}
