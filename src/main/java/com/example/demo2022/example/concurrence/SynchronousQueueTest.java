package com.example.demo2022.example.concurrence;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueTest {

    @Test
    public void synchronousQueueTest() throws InterruptedException {
        BlockingQueue<Integer> queue = new SynchronousQueue<>();
        System.out.println("queue.offer(1) = "+queue.offer(1));
        System.out.println("queue.offer(2) = "+queue.offer(2));
        System.out.println("queue.offer(3) = "+queue.offer(3));
        System.out.println("queue.take() = "+queue.take());
        System.out.println("queue.size() = "+queue.size());
    }
}
