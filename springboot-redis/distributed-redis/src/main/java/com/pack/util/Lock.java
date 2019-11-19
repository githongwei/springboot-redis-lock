package com.pack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Component
public class Lock {

    @Autowired
    private JedisPool redisPool = new RedisPoolFactory().jedisPoolFactory();

    /**
     * 加锁
     * @param key
     * @param value
     * @return
     */
    public boolean lock(String key, String value){

        Jedis jedis = null;
        Long lockWaitTimeOut = 200L;
        try{
            jedis = redisPool.getResource();
            Long deadTimeLine = System.currentTimeMillis() + lockWaitTimeOut;
            for (;;){
                String result = jedis.set(key, value, "NX", "PX", lockWaitTimeOut);
                if("OK".equals(result)){
                    return true;
                }
                lockWaitTimeOut = deadTimeLine - System.currentTimeMillis();
                if (lockWaitTimeOut <= 0L) {
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解锁
     * @param key
     * @param value
     * @return
     */
    public boolean unLock(String key, String value){
        Jedis jedis = null;
        try{
            jedis = redisPool.getResource();
            // 使用lua 去保证原子性操作
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(luaScript, Collections.singletonList(key),
                    Collections.singletonList(value));
            if ("1".equals(result)) {
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
