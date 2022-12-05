package com.example.demo2022.java.thread;

import java.util.Arrays;

public class CompleteAllThreadsDemo {
    public static void main(String[] args) {
        Thread t1 = new Thread(CompleteAllThreadsDemo::action, "t1");
        Thread t2 = new Thread(CompleteAllThreadsDemo::action, "t2");
        Thread t3 = new Thread(CompleteAllThreadsDemo::action, "t3");
        t1.start();
        t2.start();
        t3.start();

        Thread mainThread  =Thread.currentThread();
        ThreadGroup threadGroup = mainThread.getThreadGroup();

        int count  = threadGroup.activeCount();
        Thread[] threads = new Thread[count];
        threadGroup.enumerate(threads,true);
        Arrays.stream(threads).forEach(thread -> System.out.printf("当前活跃线程:[%s]...\n",thread.getName()));
    }

    private static void action() {
        System.out.printf("线程[%s]正在执行...\n", Thread.currentThread().getName());
    }
}
