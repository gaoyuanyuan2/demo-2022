package com.example.demo2022.example.gof23.singleton;

public class Singleton01 {

    //饿汉方式(线程安全)

    public static Singleton01 singleton = new Singleton01();

    private static Singleton01 getSingleton() {
        return singleton;
    }
}
