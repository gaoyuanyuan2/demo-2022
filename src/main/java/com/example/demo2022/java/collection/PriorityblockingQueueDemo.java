package com.example.demo2022.java.collection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityblockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // 自然顺序/自动扩容/不阻塞
        BlockingQueue<Integer> queue = new PriorityBlockingQueue<>(2);
        queue.put(9);
        queue.put(1);
        queue.put(8);
        System.out.println("queue.size() = " + queue.size());
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue = " + queue);
    }
}
