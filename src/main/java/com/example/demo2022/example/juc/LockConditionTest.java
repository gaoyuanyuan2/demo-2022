package com.example.demo2022.example.juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockConditionTest {

    /**
     * while(true) 没有 break 条件，从而导致了死循环。除此之外，这个实现虽然不存在死锁问题，
     * 但还是存在活锁问题的，解决活锁问题很简单，只需要随机等待一小段时间就可以了。
     */
    public static class Account {
        private int balance;
        private final Lock lock
                = new ReentrantLock();

        // 转账
        void transfer(Account tar, int amt) throws InterruptedException {
            while (true) {
                if (this.lock.tryLock()) {
                    try {
                        if (tar.lock.tryLock()) {
                            try {
                                this.balance -= amt;
                                tar.balance += amt;
                                // 新增：退出循环
                                break;
                            } finally {
                                tar.lock.unlock();
                            }
                        }//if
                    } finally {
                        this.lock.unlock();
                    }
                }//if
                // 新增：sleep 一个随机时间避免活锁
                Thread.sleep(getRandom(0,5));
            }//while
        }//transfer

        private int getRandom(int min, int max) {
            return (int) (Math.random() * (max - min) + min);
        }
    }

}
