package com.example.demo2022.java.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CountDownLatch 例子
 *
 * @author : Y
 * @since 2022/12/13 20:47
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int count = 4;
        long s1 = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        List<Future<Integer>> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Future<Integer> result = executorService.submit(new Task(countDownLatch, i));
            results.add(result);
        }
        countDownLatch.await(4, TimeUnit.SECONDS);
        results.stream().map(result -> {
            try {
                // get 也要加超时时间，不然会阻塞。
                return result.get(0, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(System.out::println);
        System.out.println("耗时：" + (System.currentTimeMillis() - s1));
        executorService.shutdown();
    }

    public static class Task implements Callable<Integer> {

        private CountDownLatch countDownLatch;

        private int param;

        @Override
        public Integer call() throws Exception {
            try {
                TimeUnit.SECONDS.sleep(5);
                return param;
            } finally {
                countDownLatch.countDown();
            }
        }

        public Task(CountDownLatch countDownLatch, int param) {
            this.countDownLatch = countDownLatch;
            this.param = param;
        }
    }

}