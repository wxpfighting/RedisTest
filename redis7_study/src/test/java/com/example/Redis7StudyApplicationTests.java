package com.example;

import com.example.mapper.LikeUserMapper;
import com.example.sample.SimpleCanalClientExample;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class Redis7StudyApplicationTests {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private LikeUserMapper likeUserMapper;

    @Test
    void hscan() {
        String bigHashKey = "map1";
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(100).build();
        Cursor<Map.Entry<Object, Object>> scanResult = opsForHash.scan(bigHashKey, scanOptions);
        while (scanResult.hasNext()) {
            Map.Entry<Object, Object> next = scanResult.next();
            opsForHash.delete(bigHashKey,next.getKey());
        }
        scanResult.close();
        //最后删除大key
        redisTemplate.delete(bigHashKey);
    }

    @Test
    void ltrim(){
        String bigListKey = "l1";
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        Long len = opsForList.size(bigListKey);
        Long count = 100L;
        while(len > 100){
            opsForList.trim(bigListKey,0,len);
            len -= count;
        }
        redisTemplate.delete(bigListKey);
    }

    @Test
    void insertSetData(){
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L);
        opsForSet.add("s2",list);
    }
    @Test
    void sScanPlusSrem(){
        String bigSetKey = "s1";
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(100).build();
        Cursor<Object> scanResult = opsForSet.scan(bigSetKey, scanOptions);
        while(scanResult.hasNext()){
            Object next = scanResult.next();
            opsForSet.remove(bigSetKey,next);
        }
        scanResult.close();
        redisTemplate.delete(bigSetKey);
    }

    @Test
    public void zScanPlusZrem(){
        String bigZSetKey = "z1";
        ZSetOperations<String, Object> opsForZSet = redisTemplate.opsForZSet();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(100).build();
        Cursor<ZSetOperations.TypedTuple<Object>> scanResult = opsForZSet.scan(bigZSetKey, scanOptions);
        while(scanResult.hasNext()){
            ZSetOperations.TypedTuple<Object> next = scanResult.next();
            opsForZSet.remove(bigZSetKey,next.getValue());
        }
        scanResult.close();
        redisTemplate.delete(bigZSetKey);
    }

    public Object doubleCheckLock(){
        final String key = "k1";
        Long userId = 2L;
        Object o = null;
        o = redisTemplate.opsForValue().get(key);
        if(o == null){
            synchronized (Redis7StudyApplicationTests.class){
                o = redisTemplate.opsForValue().get(key);
                if(o == null){
                    Long info = likeUserMapper.getById(userId);
                    if(info == null){
                        //避免缓存击穿
                        return null;
                    }else{
                        //回写缓存
                        redisTemplate.opsForValue().setIfAbsent(key,info,7L, TimeUnit.DAYS);
                    }
                }
            }
        }
        return o;
    }

    @Test
    public void testCanal(@Autowired SimpleCanalClientExample simpleCanalClientExample){
        simpleCanalClientExample.connection();
    }

    @Test
    public void testLua(){
        final String DISTRIBUTE_KEY = "distributed_key";
        String value = ""+Thread.currentThread().getId();
        //尝试加锁
        while(!redisTemplate.opsForValue().setIfAbsent(DISTRIBUTE_KEY,value,30,TimeUnit.SECONDS)){
            try{
              TimeUnit.SECONDS.sleep(2);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //成功获得锁
        /*
        执行业务代码
         */
        //利用lua脚本删除锁
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] "
                + "then return redis.call('del',KEYS[1]) "
                + "else return 0 " + "end";
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Arrays.asList(DISTRIBUTE_KEY), value);
    }

    @Test
    public void test(){
        redisTemplate.opsForValue().set("k1","v1");
    }
}
