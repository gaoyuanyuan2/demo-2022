package com.example.demo2022.example.concurrence;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {
    @Test
    public void testPriorityBlocking() throws InterruptedException {
        BlockingQueue<Integer> queue = new PriorityBlockingQueue<>(2);
        //put()方法不阻塞 排序Comparabe 或者显示传入Comparator
        queue.put(9);
        queue.put(1);
        queue.put(8);
        System.out.println("queue.size() = " + queue.size());
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue = " + queue);

    }

}
