package com.example.demo2022.java.juc;

import java.util.concurrent.TimeUnit;

public class JoinTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("s1" + System.currentTimeMillis());
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("s2" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t1.join();
        System.out.println("s3" + System.currentTimeMillis());

    }
}
