package com.example.demo2022.java.thread;

public class ShutdownHookDemo {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(ShutdownHookDemo::action, "t1"));
    }

    private static void action() {
        System.out.printf("线程[%s]正在执行...\n", Thread.currentThread().getName());
    }

}
