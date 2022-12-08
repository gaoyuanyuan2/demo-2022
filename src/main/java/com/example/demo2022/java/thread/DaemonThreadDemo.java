package com.example.demo2022.java.thread;

/**
 * 守候线程
 */
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
