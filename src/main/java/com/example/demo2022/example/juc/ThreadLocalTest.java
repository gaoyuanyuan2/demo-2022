package com.example.demo2022.example.juc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadLocalTest {
    static class ThreadId {
        static final AtomicLong
                nextId = new AtomicLong(0);
        // 定义 ThreadLocal 变量
        static final ThreadLocal<Long>
                tl = ThreadLocal.withInitial(
                () -> nextId.getAndIncrement());

        // 此方法会为每个线程分配一个唯一的 Id
        static long get() {
            return tl.get();
        }
    }

    /**
     * 解决SimpleDateFormat线程不安全
     */
    static class SafeDateFormat {
        // 定义 ThreadLocal 变量
        static final ThreadLocal<DateFormat>
                tl = ThreadLocal.withInitial(
                () -> new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"));

        static DateFormat get() {
            return tl.get();
        }
    }

    // 不同线程执行下面代码
    // 返回的 df 是不同的
    DateFormat df = SafeDateFormat.get();

    public void test() {
        ExecutorService es = null;
        ThreadLocal tl = null;
        es.execute(() -> {
            //ThreadLocal 增加变量
            tl.set(new Object());
            try {
                // 省略业务逻辑代码
            } finally {
                // 手动清理 ThreadLocal
                tl.remove();
            }
        });
    }

}
