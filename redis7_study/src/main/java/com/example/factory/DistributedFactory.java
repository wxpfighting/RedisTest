package com.example.factory;

import com.example.lock.RedisLock;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Data
public class DistributedFactory {
    public DistributedFactory(){
    }

    public static Lock createRedisLock(RedisTemplate<String,Object> redisTemplate,String lockName,Long expireTime){
        return new RedisLock(redisTemplate,lockName,UUID.randomUUID().toString(),expireTime);
    }

}
