package com.example.demo2022.example.mq.cas;

import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jdk1.8在调用sun.misc.Unsafe#getAndAddInt方法时，会根据系统底层是否支持FAA，来决定是使用FAA还是CAS。
 */
public class CASThread implements Runnable{

    private AtomicInteger total;
    private CountDownLatch latch;

    public CASThread(AtomicInteger total, CountDownLatch latch) {
        this.total = total;
        this.latch = latch;
    }

    @Override
    public void run() {
        while (!total.compareAndSet(total.get(), total.get() + 1)){}
        latch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(10000);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10000, 10000, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i < 10000; i ++) {
            executor.execute(new CASThread(atomicInteger, latch));
        }
        latch.await();
        System.out.println(atomicInteger.get());
        stopWatch.stop();
        System.out.println("消耗：" + stopWatch.getTotalTimeMillis()+ "ms");
    }
}
