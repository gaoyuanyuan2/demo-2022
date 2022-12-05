package com.example.demo2022.java.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * 保证线程顺序执行
 */
public class OrderedExecutionDemo {
    public static void main(String[] args) throws InterruptedException {
//        threadIsAlive();
//        threadSleep();
        threadWait();
    }

    private static  void threadWait() throws InterruptedException {
        Thread t1 = new Thread(OrderedExecutionDemo::action, "t1");
        Thread t2 = new Thread(OrderedExecutionDemo::action, "t2");
        Thread t3 = new Thread(OrderedExecutionDemo::action, "t3");
        threadStartAndWait(t1);
        threadStartAndWait(t2);
        threadStartAndWait(t3);
    }

    private static void threadStartAndWait(Thread thread) throws InterruptedException {
        if(Thread.State.NEW.equals(thread.getState())){
            thread.start();
        }
        while (thread.isAlive()) {
            synchronized (thread){
                thread.wait();// Jvm 唤醒
//                LockSupport.park();// 死锁发生，需要显示的被唤醒
            }
        }
    }


    private static void threadSleep() throws InterruptedException {
        Thread t1 = new Thread(OrderedExecutionDemo::action, "t1");
        Thread t2 = new Thread(OrderedExecutionDemo::action, "t2");
        Thread t3 = new Thread(OrderedExecutionDemo::action, "t3");
        t1.start();
        while (t1.isAlive()) {
            Thread.sleep(0);
        }
        t2.start();
        while (t2.isAlive()) {
            Thread.sleep(0);
        }
        t3.start();
        while (t3.isAlive()) {
            Thread.sleep(0);
        }
    }


    private static void threadIsAlive() throws InterruptedException {
        Thread t1 = new Thread(OrderedExecutionDemo::action, "t1");
        Thread t2 = new Thread(OrderedExecutionDemo::action, "t2");
        Thread t3 = new Thread(OrderedExecutionDemo::action, "t3");
        t1.start();
        while (t1.isAlive()) {
        }
        t2.start();
        while (t2.isAlive()) {
        }
        t3.start();
        while (t3.isAlive()) {
        }
    }

    private static void threadJoin() throws InterruptedException {
        Thread t1 = new Thread(OrderedExecutionDemo::action, "t1");
        Thread t2 = new Thread(OrderedExecutionDemo::action, "t2");
        Thread t3 = new Thread(OrderedExecutionDemo::action, "t3");
        t1.start();
        t1.join();
        t2.start();
        t2.join();
        t3.start();
        t3.join();
    }

    private static void action() {
        System.out.println(Thread.currentThread().getName());
    }
}
