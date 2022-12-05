package com.example.demo2022.java.thread;

import ch.qos.logback.core.hook.ShutdownHook;

import java.util.concurrent.TimeUnit;

public class DaemonThreadDemo {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
//                TimeUnit.MICROSECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Hello,World!");
        });
        /**
         * 守候线程的执行依赖于执行时间(非唯一评判)
         */
        t1.setDaemon(true);
        t1.start();
    }

}
