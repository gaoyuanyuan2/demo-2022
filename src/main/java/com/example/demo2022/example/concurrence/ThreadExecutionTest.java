package com.example.demo2022.example.concurrence;

import org.junit.Test;

public class ThreadExecutionTest {

    @Test
    public void testThread(){
        Thread t1 = new Thread(ThreadExecutionTest::action,"t1");
        Thread t2 = new Thread(ThreadExecutionTest::action,"t2");
        Thread t3 = new Thread(ThreadExecutionTest::action,"t3");

        Thread thread = new Thread(()->{action();});

        t1.start();
        t2.start();
        t3.start();
    }

    private static void action(){
        System.out.printf("线程[%s] 正在执行...\n",Thread.currentThread().getName());
    }
}
