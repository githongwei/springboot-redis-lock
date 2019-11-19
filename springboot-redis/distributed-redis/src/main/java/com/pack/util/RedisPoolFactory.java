package com.pack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisPoolFactory {

    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxActive());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait());
        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout(),redisConfig.getPassword(),0);
        return jedisPool;
    }

}
