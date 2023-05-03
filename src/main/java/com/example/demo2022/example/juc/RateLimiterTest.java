package com.example.demo2022.example.juc;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高性能限流器Guava
 */
public class RateLimiterTest {
    @Test
    public void test() {
        // 限流器流速：2 个请求 / 秒
        RateLimiter limiter = RateLimiter.create(2.0);
        // 执行任务的线程池
        ExecutorService es = Executors.newFixedThreadPool(1);
        // 记录上一次执行时间
        AtomicLong prev = new AtomicLong(System.nanoTime());
        // 测试执行 20 次
        for (int i = 0; i < 20; i++) {
            // 限流器限流
            limiter.acquire();
            // 提交任务异步执行
            es.execute(() -> {
                long cur = System.nanoTime();
                // 打印时间间隔：毫秒
                System.out.println((cur - prev.get()) / 1000_000);
                prev.set(cur);
            });
        }
    }

}
