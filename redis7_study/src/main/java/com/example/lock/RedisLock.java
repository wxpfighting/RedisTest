package com.example.lock;

import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Data
public class RedisLock implements Lock {
    private RedisTemplate<String,Object> redisTemplate;
    private final String lockName;
    private final String operator;
    private Long expireTime;

    public RedisLock(RedisTemplate<String,Object> redisTemplate,String lockName,String uuid,Long expireTime){
        this.redisTemplate = redisTemplate;
        this.lockName = lockName;
        this.operator = uuid + ": " +Thread.currentThread().getId();
        this.expireTime = expireTime;
    }
    @Override
    public void lock() {
        tryLock();
    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(-1L,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if(time == -1L){
            //尝试获得锁
            String lockScript = "if redis.call('exists',KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then " +
                    "redis.call('hincrby',KEYS[1],ARGV[1],1) " +
                    "redis.call('expire',KEYS[1],ARGV[2]) " +
                    "return 1 " +
                    "else return 0 " +
                    "end";
            while(Boolean.FALSE.equals(redisTemplate.execute(new DefaultRedisScript<>(lockScript, Boolean.class), Arrays.asList(lockName), operator, String.valueOf(expireTime)))) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            System.out.println(operator + "获得了锁");
            delay();
            return true;
        }
        return false;
    }

    private void delay(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String timeScript = "if redis.call('hexists',KEYS[1],ARGV[1]) == 1 then "
                        + "redis.call('expire',KEYS[1],ARGV[2]) return 1 " +
                        "else return 0 " +
                        "end";
                if(Boolean.TRUE.equals(redisTemplate.execute(new DefaultRedisScript<>(timeScript,Boolean.class),Arrays.asList(lockName),operator,String.valueOf(expireTime)))){
                    System.out.println("延时");
                    delay();
                }
            }
        },10 * 1000);
    }
    @Override
    public void unlock() {
        String releaseLock = "if redis.call('hexists',KEYS[1],ARGV[1]) == 0 then " +
                "return nil " +
                "elseif redis.call('hincrby',KEYS[1],ARGV[1],-1) == 0 then " +
                "return redis.call('del',KEYS[1]) " +
                "else return 0 " +
                "end";
        Long flag = redisTemplate.execute(new DefaultRedisScript<>(releaseLock,Long.class),Arrays.asList(lockName),operator);
        if(flag == null){
            throw new RuntimeException("锁已被释放或当前线程并非锁的持有者");
        }
        System.out.println(operator + "释放了锁");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
