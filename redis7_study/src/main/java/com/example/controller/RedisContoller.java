package com.example.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/redis")
public class RedisContoller {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final Log LOGG = LogFactory.getLog(RedisContoller.class);

    public static final String ORDER_KEY = "ord:";

    @GetMapping("/createOrder")
    public String createOrder(){
        System.out.println("test");
        Integer keyId = ThreadLocalRandom.current().nextInt(1000)+1;
        String serialNo = UUID.randomUUID().toString();

        String key = ORDER_KEY + keyId;
        String value = "京东：" + serialNo;
        LOGG.info(key + " " + value);

        stringRedisTemplate.opsForValue().set(key,value);
        return "ok";
    }

    @GetMapping("/getOrder/{id}")
    public String getOrder(@PathVariable("id") String id){
        return stringRedisTemplate.opsForValue().get(id);
    }

}
