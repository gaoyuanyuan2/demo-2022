package com.example.demo2022.java.juc;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class SemaphoreDemo {
    public static void main(String[] args) throws InterruptedException {
        Semaphore cyclicBarrier = new Semaphore(3);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        List<Future<String>> futureList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            final int j = i;
            Future<String> future = threadPool.submit(new SemaphoreDemo.Task(j, cyclicBarrier));
            futureList.add(future);
        }
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

        private Semaphore cyclicBarrier;

        public Task(int j, Semaphore cyclicBarrier) {
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
                cyclicBarrier.acquire();
            }
            System.out.println(System.currentTimeMillis() + "结束:" + j);
            return j + ",OK!";
        }
    }
}