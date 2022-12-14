package com.example.demo2022.java.collection;

import java.util.concurrent.*;

/**
 * 阻塞队里对比
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws Exception {

        offer(new ArrayBlockingQueue<>(2));

        offer(new LinkedBlockingQueue<>(2));

        offer(new PriorityBlockingQueue<>(2));

        offer(new SynchronousQueue<>());
    }

    private static void offer(BlockingQueue<Integer> queue) throws Exception {
        System.out.println("queue.getClass() = " + queue.getClass().getName());
        System.out.println("queue.offer(1) = " + queue.offer(1));
        System.out.println("queue.offer(2) = " + queue.offer(2));
        System.out.println("queue.offer(3) = " + queue.offer(3));
        System.out.println("queue.size() = " + queue.size());
        System.out.println("queue.take() = " + queue.take());
    }
}
