package com.example.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置键和值的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //java->redis
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        //redis->java
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        //设置hash的键和值的序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        //初始化redisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
