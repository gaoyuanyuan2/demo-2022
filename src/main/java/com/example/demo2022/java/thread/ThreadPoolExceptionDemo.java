package com.example.demo2022.java.thread;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExceptionDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>()) {
            /**
             * 通过覆盖{@Link ThreadPool Executor#afterExecute ( Runnable, Throwable)} 达到获取异常的信息
             * @param r
             * @param t
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.printf("线程[%s] 遇到了异常，详细信息: %s\n"
                        , Thread.currentThread().getName()
                        , t.getMessage()
                );
            }
        };
        /**
         * execute会直接抛出任务执行时的异常，可以用try、catch来捕获，和普通线程的处理方式完全一致。
         * submit会吃掉异常，可通过Future的get方法将任务执行时的异常重新抛出。
         */
        threadPoolExecutor.execute(() -> {
            throw new RuntimeException("数据达到阈值");
        });

        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
        threadPoolExecutor.shutdown();
    }
}
