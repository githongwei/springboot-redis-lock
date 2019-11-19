package com.pack.controller;

import com.pack.util.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@RestController
@RequestMapping("/index")
public class IndexController {

    private static LongAdder longAdder = new LongAdder();
    private static Long LOCK_EXPIRE_TIME = 200L;
    private static Long stock = 10000L;

    @Autowired
    private Lock lock = new Lock();

    static {
        longAdder.add(1000);
    }

    @RequestMapping("/")
    public String index(){

      return "小熊熊";
    }

    @RequestMapping("/seckill")
    public String indexSeckill(){
        String uuid = UUID.randomUUID().toString().substring(0,32);
        Long time = System.currentTimeMillis() + LOCK_EXPIRE_TIME;
        if (!lock.lock(uuid, "redis-seckill")) {
            return "人太多了，换个姿势操作一下";
        }

        if (longAdder.longValue() == 0L) {
            return "已抢光";
        }

        doSomeThing();

        if (longAdder.longValue() == 0L) {
            return "已抢光";
        }

        longAdder.decrement();

        lock.unLock(uuid, "redis-seckill");

        Long stock = longAdder.longValue();
        Long bought = 10000L - stock;
        return "已抢" + bought + ", 还剩下" + stock;
    }

    public void doSomeThing() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
