package com.example.demo2022.example.gof23.singleton;

public class Singleton02 {

    //懒汉式（非线程安全)

    public static Singleton02 singleton;

    private static Singleton02 getSingleton() {
        if (singleton == null) {
            singleton = new Singleton02();
        }
        return singleton;
    }

}
