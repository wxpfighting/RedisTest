package com.example.controller;

import com.example.factory.DistributedFactory;
import com.example.lock.RedisLock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RestController
@RequestMapping("/redis/testDistribute")
@Slf4j
@Api(tags = "测试分布式锁")
public class DistributedController {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private Redisson redisson;
    @Resource
    private Redisson redisson1;
    @Resource
    private Redisson redisson2;
    @Resource
    private Redisson redisson3;
    @GetMapping("/test")
    @ApiOperation("测试分布式锁")
    public void testDistribute(){
        //Lock redisLock = DistributedFactory.createRedisLock(redisTemplate, "buyLock", 50L);
        //RLock redisLock = redisson.getLock("myLock");
        RLock myLock1 = redisson1.getLock("myLock");
        RLock myLock2 = redisson2.getLock("myLock");
        RLock myLock3 = redisson3.getLock("myLock");

        RedissonMultiLock redisLock = new RedissonMultiLock(myLock1, myLock2, myLock3);
        try{
            redisLock.lock();
            redisTemplate.opsForValue().increment("goods",-1);
            log.info("商品的数量减一,操作的锁是线程是: {}" ,Thread.currentThread().getId() );
            TimeUnit.SECONDS.sleep(30);
            tryEntry(redisLock);
            String[] a = {"1","2"};
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //redisLock.unlock();
            /*if(redisLock.isLocked() && redisLock.isHeldByCurrentThread()){
                redisLock.unlock();
            }*/
            System.out.println("multi lock has been released");
            redisLock.unlock();
        }
    }

    public void tryEntry(Lock redisLock){
        try{
            redisLock.lock();
        }finally {
            redisLock.unlock();
        }
    }

}
