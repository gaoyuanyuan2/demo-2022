package com.example.demo2022.java.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VolatileAtomicDemo {

    private static volatile int count = 0;
    
    private static AtomicInteger atomicCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 1000; i++) {
            executorService.submit(VolatileAtomicDemo::add);
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println("count：" + count);
        System.out.println("atomicCount：" + atomicCount.get());
        executorService.shutdown();
    }

    private static void add() {
        count++;
        atomicCount.addAndGet(1);
    }
}