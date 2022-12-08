package com.example.demo2022.java.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        int barrierCount = 3;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(barrierCount + 1);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        List<Future<String>> futureList = new ArrayList<>(barrierCount);
        for (int i = 0; i < barrierCount; i++) {
            final int j = i;
            Future<String> future = threadPool.submit(new Task(j, cyclicBarrier));
            futureList.add(future);
        }
        cyclicBarrier.await();
        // 所有结果返回后再获取结果
        futureList.stream().map(e -> {
            try {
                return e.get(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }).forEach(System.out::println);
        System.out.println(System.currentTimeMillis());
        threadPool.shutdown();
    }

    private static class Task implements Callable<String> {

        private int j;

        private CyclicBarrier cyclicBarrier;

        public Task(int j, CyclicBarrier cyclicBarrier) {
            this.j = j;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public String call() throws Exception {
            System.out.println(System.currentTimeMillis() + "开始：" + j);
            try {
                TimeUnit.SECONDS.sleep(j);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                cyclicBarrier.await();
            }
            System.out.println(System.currentTimeMillis() + "结束:" + j);
            return j + ",OK!";
        }
    }
}