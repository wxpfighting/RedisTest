package com.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {
    @Bean
    public Redisson redisson(){
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://192.168.175.133:6379")
                .setPassword("123456");
        return (Redisson) Redisson.create(config);
    }

    @Bean
    public Redisson redisson1(){
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://192.168.175.133:6379")
                .setPassword("123456");
        return (Redisson) Redisson.create(config);
    }

    @Bean
    public Redisson redisson2(){
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://192.168.175.133:6380")
                .setPassword("123456");
        return (Redisson) Redisson.create(config);
    }

    @Bean
    public Redisson redisson3(){
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://192.168.175.133:6381")
                .setPassword("123456");
        return (Redisson) Redisson.create(config);
    }




}
