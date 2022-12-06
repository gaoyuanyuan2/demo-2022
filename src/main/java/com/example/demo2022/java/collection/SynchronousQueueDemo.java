package com.example.demo2022.java.collection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueDemo {

    public static void main(String[] args) throws Exception {
        // 1. SynchronousQueue 是无空间，offer 永远返回false
        // 2. SynchronousQueue take()方法会被阻塞，必须被其他线程显示地调用put(Object);
        BlockingQueue<Integer> queue = new SynchronousQueue<>();
        System.out.println("queue.offer(1) = " + queue.offer(1));
        System.out.println("queue.offer(2) = " + queue.offer(2));
        System.out.println("queue.offer(3) = " + queue.offer(3));
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue.size =" + queue.size());
    }
}
