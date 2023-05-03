package com.example.demo2022.example.juc;

import java.util.concurrent.locks.StampedLock;

/**
 * 读写锁，锁的申请和释放要成对出现
 */
public class StampedLockTest {
    private double x, y;
    final StampedLock sl = new StampedLock();

    // 存在问题的方法
    void moveIfAtOrigin(double newX, double newY) {
        long stamp = sl.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    // 问题出在没有对 stamp 重新赋值
                    // 新增下面一行
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            // 此处 unlock 的是 stamp
            sl.unlock(stamp);
        }
    }
}
