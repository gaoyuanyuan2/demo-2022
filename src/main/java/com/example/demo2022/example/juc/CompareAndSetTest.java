package com.example.demo2022.example.juc;

import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSetTest {
    /**
     * 实际上却存在严重的并发问题，问题就出在对 or 的赋值在 while 循环之外，这样每次循环 or
     * 的值都不会发生变化，所以一旦有一次循环 rf.compareAndSet(or, nr) 的值等于 false，
     * 那之后无论循环多少次，都会等于 false。也就是说在特定场景下，变成了 while(true) 问题。
     * 既然找到了原因，修改就很简单了，只要把对 or 的赋值移到 while 循环之内就可以了。
     */
    public static class SafeWM {

        final AtomicReference<WMRange>
                rf = new AtomicReference<>(
                new WMRange(0, 0)
        );

        // 设置库存上限
        void setUpper(int v) {
            WMRange nr;
            WMRange or;
            // 原代码在这里
            //WMRange or=rf.get();
            do {
                // 移动到此处
                // 每个回合都需要重新获取旧值
                or = rf.get();
                // 检查参数合法性
                if (v < or.lower) {
                    throw new IllegalArgumentException();
                }
                nr = new WMRange(v, or.lower);
            } while (!rf.compareAndSet(or, nr));
        }
    }

    public static class WMRange {
        final int upper;
        final int lower;

        public WMRange(int upper, int lower) {
            this.upper = upper;
            // 省略构造函数实现
            this.lower = lower;
        }
    }
}
