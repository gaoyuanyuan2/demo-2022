package com.example.demo2022.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by yan on  12/10/2018.
 *
 * http://mzorro.me/2017/10/25/redis-distributed-lock/
 *
 *  tryLock是一个非阻塞的分布式锁方法，在获得锁失败后会立即返回。如果需要一个阻塞式的锁方法，
 *  可以将tryLock方法包装为轮询（以一定的时间间隔来轮询，这很重要，否则Redis会吃不消！）。
 *
 *  此种方法看似没有什么问题，但其实则有一个漏洞：在加锁的过程中，客户端顺序的向Redis服务器发送了SETNX和EXPIRE命令，
 *  那么假设在SETNX命令执行完成之后，在EXPIRE命令发出去之前客户端发生崩溃（或客户端与Redis服务器的网络连接突然断掉），
 *  导致EXPIRE命令没有得到执行，其他客户端将会发生永久死锁！
 */
@Service
public class DistributedLock {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private boolean tryLock(String lockName, RedisConnection conn, int lockSeconds) throws Exception {
        byte[] lockKey = lockName.getBytes();
        long nowTime = System.currentTimeMillis();
        long expireTime = nowTime + lockSeconds * 1000 + 1000; // 容忍不同服务器时间有1秒内的误差
        if (conn.setNX(lockKey, longToBytes(expireTime))) {
            conn.expire(lockKey, lockSeconds);
            return true;
        } else {
            byte[] oldValue = conn.get(lockKey);
            if (oldValue != null && bytesToLong(oldValue) < nowTime) {
                // 这个锁已经过期了，可以获得它
                // 如果setNX和expire之间客户端发生崩溃，可能会出现这样的情况
                byte[] oldValue2 = conn.getSet(lockKey, longToBytes(expireTime));
                if (Arrays.equals(oldValue, oldValue2)) {
                    // 获得了锁
                    conn.expire(lockKey, lockSeconds);
                    return true;
                } else {
                    // 被别人抢占了锁(此时已经修改了lockKey中的值，不过误差很小可以忽略)
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 尝试获得锁，成功返回true，如果失败或异常立即返回false
     * @param lockSeconds 加锁的时间(秒)，超过这个时间后锁会自动释放
     */
    public boolean tryLock(String lockName, final int lockSeconds) {
        return stringRedisTemplate.execute((RedisConnection conn) -> {
            try {
                return tryLock(lockName, conn, lockSeconds);
            } catch (Exception e) {
                logger.error("tryLock Error", e);
                return false;
            }
        });
    }

    /**
     * 轮询的方式去获得锁，成功返回true，超过轮询次数或异常返回false
     *
     * @param lockSeconds       加锁的时间(秒)，超过这个时间后锁会自动释放
     * @param tryIntervalMillis 轮询的时间间隔(毫秒)
     * @param maxTryCount       最大的轮询次数
     */
    public boolean tryLock(String lockName, final int lockSeconds, final long tryIntervalMillis, final int
            maxTryCount) {
        return stringRedisTemplate.execute((RedisConnection conn) -> {
            int tryCount = 0;
            while (true) {
                if (++tryCount >= maxTryCount) {
                    // 获取锁超时
                    return false;
                }
                try {
                    if (tryLock(lockName, conn, lockSeconds)) {
                        return true;
                    }
                } catch (Exception e) {
                    logger.error("tryLock Error", e);
                    return false;
                }
                try {
                    Thread.sleep(tryIntervalMillis);
                } catch (InterruptedException e) {
                    logger.error("tryLock interrupted", e);
                    return false;
                }
            }
        });

    }

    /**
     * 如果加锁后的操作比较耗时，调用方其实可以在unlock前根据时间判断下锁是否已经过期
     * 如果已经过期可以不用调用，减少一次请求
     */
    public void unlock(String lockName) {
        stringRedisTemplate.delete(lockName);
    }

    public byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.putLong(value);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        if (bytes.length != Long.SIZE / Byte.SIZE) {
            throw new IllegalArgumentException("wrong length of bytes!");
        }
        return ByteBuffer.wrap(bytes).getLong();
    }


}
