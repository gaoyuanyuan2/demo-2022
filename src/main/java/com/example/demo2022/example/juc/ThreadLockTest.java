package com.example.demo2022.example.juc;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用线程池过程中，还要注意一种线程死锁的场景。如果提交到相同线程池的任务不是相互独立的，
 * 而是有依赖关系的，那么就有可能导致线程死锁。
 */
public class ThreadLockTest {

    @Test
    public void test() throws InterruptedException {
        //L1、L2 阶段共用的线程池
        ExecutorService es = Executors.
                newFixedThreadPool(2);
        //L1 阶段的闭锁
        CountDownLatch l1 = new CountDownLatch(2);
        for (
                int i = 0;
                i < 2; i++) {
            System.out.println("L1");
            // 执行 L1 阶段任务
            es.execute(() -> {
                //L2 阶段的闭锁
                CountDownLatch l2 = new CountDownLatch(2);
                // 执行 L2 阶段子任务
                for (int j = 0; j < 2; j++) {
                    es.execute(() -> {
                        System.out.println("L2");
                        l2.countDown();
                    });
                }
                // 等待 L2 阶段任务执行完
                try {
                    l2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                l1.countDown();
            });
        }
        // 等着 L1 阶段任务执行完
        l1.await();
        System.out.println("end");
    }
}
