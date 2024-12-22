package com.example.hyperloglog;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;

@Component
public class HyperLogLogTest {
    private static final String hyperKey = "hyper1";
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @PostConstruct
    private void init(){
        for(int i = 0; i < 200; ++i){
            Random random = new Random();
            String ipAddress = random.nextInt(256) + "."
                    + random.nextInt(256) + "."
                    + random.nextInt(256) + "."
                    + random.nextInt(256);
            redisTemplate.opsForHyperLogLog().add(hyperKey,ipAddress);
        }
    }

    public Long statistic(){
        return redisTemplate.opsForHyperLogLog().size(hyperKey);
    }

}
