package com.example.demo2022.example.mq.lru;

/**
 * KV 存储抽象
 */
public interface Storage<K,V> {
    /**
     * 根据提供的 key 来访问数据
     * @param key 数据 Key
     * @return 数据值
     */
    V get(K key);
}


