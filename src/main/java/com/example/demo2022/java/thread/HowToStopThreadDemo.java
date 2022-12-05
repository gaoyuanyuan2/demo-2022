package com.example.demo2022.java.thread;

// 停止线程是不可能的，只能停止逻辑
public class HowToStopThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            if (!Thread.currentThread().isInterrupted()) {
                action();
            }
        }, "t1");
        t1.start();
        t1.interrupt();
        t1.join();
    }

    private static void action() {
        System.out.println(Thread.currentThread().getName() + "线程执行完成！");
    }
}
