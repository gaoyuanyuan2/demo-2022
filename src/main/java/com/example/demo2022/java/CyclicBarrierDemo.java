package com.example.demo2022.java;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            final int j = i;
            threadPool.submit(() -> {
                System.out.println(System.currentTimeMillis() + "开始：" + j);
                try {
                    TimeUnit.SECONDS.sleep(j);
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + "结束:" + j);
            });
        }
        TimeUnit.SECONDS.sleep(4);
        System.out.println(System.currentTimeMillis() + "全部结束");
        threadPool.shutdown();

    }
}