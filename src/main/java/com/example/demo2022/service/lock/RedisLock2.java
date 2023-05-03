package com.example.demo2022.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by yan on  12/10/2018.
 *
 * 经过慎重考虑，发现以上实现的分布式锁有一个较为严重的解锁漏洞：因为解锁操作只是做了简单的DEL KEY，
 * 如果某客户端在获得锁后执行业务的时间超过了锁的过期时间，则最后的解锁操作会误解掉其他客户端的操作。
 *
 * 为解决此问题，我们在创建RedisLock对象时用本机时间戳和UUID来创建一个绝对唯一的lockValue，然后在加锁时存入此值，
 * 并在解锁前用GET取出值进行比较，如果匹配才做DEL。这里依然需要用LUA脚本保证整个解锁过程的原子性。
 *
 * Redis实现的分布式锁(不可重入)
 * 此对象非线程安全，使用时务必注意
 */
public class RedisLock2 {

    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private final StringRedisTemplate stringRedisTemplate;

    private final String lockKey;

    private final String lockValue;

    private boolean locked = false;

    /**
     * 使用脚本在redis服务器执行这个逻辑可以在一定程度上保证此操作的原子性
     * （即不会发生客户端在执行setNX和expire命令之间，发生崩溃或失去与服务器的连接导致expire没有得到执行，发生永久死锁）
     * <p>
     * 除非脚本在redis服务器执行时redis服务器发生崩溃，不过此种情况锁也会失效
     */
    private static final RedisScript<Boolean> SETNX_AND_EXPIRE_SCRIPT;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if (redis.call('setnx', KEYS[1], ARGV[1]) == 1) then\n");
        sb.append("\tredis.call('expire', KEYS[1], tonumber(ARGV[2]))\n");
        sb.append("\treturn true\n");
        sb.append("else\n");
        sb.append("\treturn false\n");
        sb.append("end");
        SETNX_AND_EXPIRE_SCRIPT = new RedisScriptImpl<Boolean>(sb.toString(), Boolean.class);
    }

    private static final RedisScript<Boolean> DEL_IF_GET_EQUALS;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if (redis.call('get', KEYS[1]) == ARGV[1]) then\n");
        sb.append("\tredis.call('del', KEYS[1])\n");
        sb.append("\treturn true\n");
        sb.append("else\n");
        sb.append("\treturn false\n");
        sb.append("end");
        DEL_IF_GET_EQUALS = new RedisScriptImpl<Boolean>(sb.toString(), Boolean.class);
    }

    public RedisLock2(StringRedisTemplate stringRedisTemplate, String lockKey) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockKey = lockKey;
        this.lockValue = UUID.randomUUID().toString() + "." + System.currentTimeMillis();
    }

    private boolean doTryLock(int lockSeconds) throws Exception {
        if (locked) {
            throw new IllegalStateException("already locked!");
        }
        locked = stringRedisTemplate.execute(SETNX_AND_EXPIRE_SCRIPT, Collections.singletonList(lockKey), lockValue,
                String.valueOf(lockSeconds));
        return locked;
    }

    /**
     * 尝试获得锁，成功返回true，如果失败立即返回false
     *
     * @param lockSeconds 加锁的时间(秒)，超过这个时间后锁会自动释放
     */
    public boolean tryLock(int lockSeconds) {
        try {
            return doTryLock(lockSeconds);
        } catch (Exception e) {
            logger.error("tryLock Error", e);
            return false;
        }
    }

    /**
     * 轮询的方式去获得锁，成功返回true，超过轮询次数或异常返回false
     *
     * @param lockSeconds       加锁的时间(秒)，超过这个时间后锁会自动释放
     * @param tryIntervalMillis 轮询的时间间隔(毫秒)
     * @param maxTryCount       最大的轮询次数
     */
    public boolean tryLock(final int lockSeconds, final long tryIntervalMillis, final int maxTryCount) {
        int tryCount = 0;
        while (true) {
            if (++tryCount >= maxTryCount) {
                // 获取锁超时
                return false;
            }

            try {
                if (doTryLock(lockSeconds)) {
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
    }

    /**
     * 解锁操作
     */
    public void unlock() {
        if (!locked) {
            throw new IllegalStateException("not locked yet!");
        }
        locked = false;

        // 忽略结果
        stringRedisTemplate.execute(DEL_IF_GET_EQUALS, Collections.singletonList(lockKey), lockValue);
    }

    private static class RedisScriptImpl<T> implements RedisScript<T> {

        private final String script;

        private final String sha1;

        private final Class<T> resultType;

        public RedisScriptImpl(String script, Class<T> resultType) {
            this.script = script;
            this.sha1 = DigestUtils.sha1DigestAsHex(script);
            this.resultType = resultType;
        }

        @Override
        public String getSha1() {
            return sha1;
        }

        @Override
        public Class<T> getResultType() {
            return resultType;
        }

        @Override
        public String getScriptAsString() {
            return script;
        }
    }
}
