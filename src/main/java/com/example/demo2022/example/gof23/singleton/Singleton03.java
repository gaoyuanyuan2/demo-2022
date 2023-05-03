package com.example.demo2022.example.gof23.singleton;

public class Singleton03 {

    //懒汉式（线程安全)

    public static Singleton03 singleton;

    private static synchronized Singleton03 getSingleton() {
        if (singleton == null) {
            singleton = new Singleton03();
        }
        return singleton;
    }

}
