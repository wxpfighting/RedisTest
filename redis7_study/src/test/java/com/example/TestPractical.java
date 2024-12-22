package com.example;

import com.example.geo.GeoTest;
import com.example.hyperloglog.HyperLogLogTest;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestPractical {
    @Resource
    private HyperLogLogTest hyperLogLogTest;
    @Resource
    private GeoTest geoTest;

    @Test
    public void testHyperLogLog(){
        System.out.println(hyperLogLogTest.statistic());
    }

    @Test
    public void testGeo(){
        System.out.println("+++++++++++++++++++++++++添加+++++++++++++++++++++");
        geoTest.add();
        System.out.println("+++++++++++++++++++++++++hash+++++++++++++++++++++");
        System.out.println(geoTest.getHash());
        System.out.println("+++++++++++++++++++++++++距离+++++++++++++++++++++");
        System.out.println(geoTest.getDistance());
        System.out.println("+++++++++++++++++++++++++radius+++++++++++++++++++++");
        System.out.println(geoTest.getRadius());
        System.out.println("+++++++++++++++++++++++++radiusByMember+++++++++++++++++++++");
        System.out.println(geoTest.getRadiusByMember());
    }

    @Test
    public void testThread(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 20, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1024),
                new ThreadFactoryBuilder().setNameFormat("factory-name-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        executor.execute(()-> System.out.println(Thread.currentThread().getName()));
    }



}
