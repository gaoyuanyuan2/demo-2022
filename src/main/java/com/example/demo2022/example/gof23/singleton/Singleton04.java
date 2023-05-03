package com.example.demo2022.example.gof23.singleton;

/**
 * 双重检查（Double Check）方案
 */
public class Singleton04 {
    private static volatile
    Singleton04 singleton04;
    // 构造方法私有化
    private Singleton04() {}
    // 获取实例（单例）
    public static Singleton04
    getInstance() {
        // 第一次检查
        if(singleton04 ==null){
            synchronized (Singleton04.class){
                // 获取锁后二次检查
                if(singleton04 ==null){
                    singleton04 =new Singleton04();
                }
            }
            }
            return singleton04;
        }
    }
