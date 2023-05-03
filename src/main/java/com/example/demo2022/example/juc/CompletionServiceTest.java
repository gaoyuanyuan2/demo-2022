package com.example.demo2022.example.juc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CompletionServiceTest {
    @Test
    public void test() throws InterruptedException {
        // 创建线程池
        ExecutorService executor =
                Executors.newFixedThreadPool(3);
        // 异步向电商 S1 询价
        Future<Integer> f1 =
                executor.submit(
                        ()->getPriceByS1());
        // 异步向电商 S2 询价
        Future<Integer> f2 =
                executor.submit(
                        ()->getPriceByS2());
        // 异步向电商 S3 询价
        Future<Integer> f3 =
                executor.submit(
                        ()->getPriceByS3());

        // 创建阻塞队列
        BlockingQueue<Integer> bq =
                new LinkedBlockingQueue<>();
        // 电商 S1 报价异步进入阻塞队列
        executor.execute(() ->
                putValue(bq,f1));

        // 电商 S2 报价异步进入阻塞队列
        executor.execute(() ->
                putValue(bq,f2));
        // 电商 S3 报价异步进入阻塞队列
        executor.execute(() ->
                putValue(bq,f3));
        // 异步保存所有报价
        for (int i = 0; i < 3; i++) {
            Integer r = bq.take();
            executor.execute(() -> save(r));
        }
    }

    @Test
    public void test2() throws InterruptedException, ExecutionException {
        // 创建线程池
        ExecutorService executor =
                Executors.newFixedThreadPool(3);
        // 创建 CompletionService
        CompletionService<Integer> cs = new
                ExecutorCompletionService<>(executor);
        // 异步向电商 S1 询价
        cs.submit(()->getPriceByS1());
        // 异步向电商 S2 询价
        cs.submit(()->getPriceByS2());
        // 异步向电商 S3 询价
        cs.submit(()->getPriceByS3());
        // 将询价结果异步保存到数据库
        for (int i=0; i<3; i++) {
            Integer r = cs.take().get();
            executor.execute(()->save(r));
        }
    }

    /**
     * 那个先返回就返回哪个。
     */
    @Test
    public Integer test3() throws InterruptedException, ExecutionException {
        // 创建线程池
        ExecutorService executor =
                Executors.newFixedThreadPool(3);
// 创建 CompletionService
        CompletionService<Integer> cs =
                new ExecutorCompletionService<>(executor);
// 用于保存 Future 对象
        List<Future<Integer>> futures =
                new ArrayList<>(3);
// 提交异步任务，并保存 future 到 futures
        futures.add(
                cs.submit(()->geocoderByS1()));
        futures.add(
                cs.submit(()->geocoderByS2()));
        futures.add(
                cs.submit(()->geocoderByS3()));
// 获取最快返回的任务执行结果
        Integer r = 0;
        try {
            // 只要有一个成功返回，则 break
            for (int i = 0; i < 3; ++i) {
                r = cs.take().get();
                // 简单地通过判空来检查是否成功返回
                if (r != null) {
                    break;
                }
            }
        } finally {
            // 取消所有任务
            for(Future<Integer> f : futures)
                f.cancel(true);
        }
// 返回结果
        return r;
    }

    private Integer geocoderByS2() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return 1;
    }

    private Integer geocoderByS3() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return 1;
    }

    private Integer geocoderByS1() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return 1;
    }

    private void putValue(BlockingQueue<Integer> bq, Future<Integer> f) {
        try {
            bq.put(f.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        };
    }

    List<Integer> list = new ArrayList<>();
    private void save(Integer r) {
        list.add(r);
    }

    private int getPriceByS3() {
        return 1;
    }

    private int getPriceByS2() {
        return 2;
    }

    private int getPriceByS1() {
        return 3;
    }
}
