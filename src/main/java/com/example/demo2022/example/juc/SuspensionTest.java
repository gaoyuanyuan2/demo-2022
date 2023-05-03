package com.example.demo2022.example.juc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Guarded 丨 Suspension模式：等待唤醒机制的规范实现
 */
public class SuspensionTest {
    class Message {
        String id;
        String content;

        public Message(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    // 该方法可以发送消息
    void send(Message msg) {
        // 省略相关代码
    }

    // MQ 消息返回后会调用该方法
    // 该方法的执行线程不同于
    // 发送消息的线程
    void onMessage(Message msg) {
        // 省略相关代码
    }

    // 处理浏览器发来的请求
    String handleWebReq() {
        // 创建一消息
        Message msg1 = new
                Message("1", "{...}");
        // 发送消息
        send(msg1);
        // 如何等待 MQ 返回的消息呢？
        String result = "...";
        return result;
    }

    // 处理浏览器发来的请求
    String handleWebReq2() {
        String id = UUID.randomUUID().toString();
        // 创建一消息
        Message msg1 = new Message(id, "{...}");
        // 创建 GuardedObject 实例
        GuardedObject2<Message> go = GuardedObject2.create(id);
        // 发送消息
        send(msg1);
        // 等待 MQ 消息
        Message r = go.get(t -> t != null);
        return r.content;
    }

    void onMessage2(Message msg) {
        // 唤醒等待的线程
        GuardedObject2.fireEvent(msg.id, msg);
    }

}

/**
 * 实现
 *
 * @param <T>
 */
class GuardedObject<T> {
    // 受保护的对象
    T obj;
    final Lock lock = new ReentrantLock();
    final Condition done = lock.newCondition();
    final int timeout = 1;

    // 获取受保护对象
    T get(Predicate<T> p) {
        lock.lock();
        try {
            //MESA 管程推荐写法
            while (!p.test(obj)) {
                done.await(timeout, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        // 返回非空的受保护对象
        return obj;
    }

    // 事件通知方法
    void onChanged(T obj) {
        lock.lock();
        try {
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 扩展后的 GuardedObject 内部维护了一个 Map，其 Key 是 MQ 消息 id，而 Value 是 GuardedObject
 * 对象实例，同时增加了静态方法 create() 和 fireEvent()；create() 方法用来创建一个 GuardedObject
 * 对象实例，并根据 key 值将其加入到 Map 中。
 * @param <T>
 */
class GuardedObject2<T> {
    // 受保护的对象
    T obj;
    final Lock lock =
            new ReentrantLock();
    final Condition done =
            lock.newCondition();
    final int timeout = 2;
    // 保存所有 GuardedObject
    final static Map<Object, GuardedObject2>
            gos = new ConcurrentHashMap<>();

    // 静态方法创建 GuardedObject
    static <K> GuardedObject2
    create(K key) {
        GuardedObject2 go = new GuardedObject2();
        gos.put(key, go);
        return go;
    }

    static <K, T> void
    fireEvent(K key, T obj) {
        GuardedObject2 go = gos.remove(key);
        if (go != null) {
            go.onChanged(obj);
        }
    }

    // 获取受保护对象
    T get(Predicate<T> p) {
        lock.lock();
        try {
            //MESA 管程推荐写法
            while (!p.test(obj)) {
                done.await(timeout,
                        TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        // 返回非空的受保护对象
        return obj;
    }

    // 事件通知方法
    void onChanged(T obj) {
        lock.lock();
        try {
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }
}