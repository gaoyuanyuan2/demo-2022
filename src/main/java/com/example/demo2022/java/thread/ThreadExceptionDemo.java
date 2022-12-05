package com.example.demo2022.java.thread;

/**
 *
 */
public class ThreadExceptionDemo {
    public static void main(String[] args) throws InterruptedException {

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                    System.out.printf("线程[%s] 遇到了异常，详细信息: %s\n"
                            , thread.getName()
                            , throwable.getMessage()
                    );
                }
        );
        Thread t1 = new Thread(() -> {
            throw new RuntimeException("数据达到阈值");
        }, "t1");
        t1.start();
        t1.join();
        // Java Thread 是一个包装， 它由GC做垃圾回收
        System.out.println(t1.isAlive());
    }
}
