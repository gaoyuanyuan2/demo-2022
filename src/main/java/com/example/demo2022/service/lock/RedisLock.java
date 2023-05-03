package com.example.demo2022.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by yan on  12/10/2018.
 * 上面的分布式锁还有一个问题，那就是服务器之间时间同步的问题。在分布式场景中，多台服务器之间的时间做到同步是非常困难的，
 * 所以在代码中我加了1秒的时间容错，但依赖服务器时间的同步还是可能会不靠谱的。
 *从Redis 2.6开始，客户端可以直接向Redis服务器提交Lua脚本，也就是说可以直接在Redis服务器来执行一些较复杂的逻辑，
 * 而此脚本的提交对于客户端来说是相对原子性的。这恰好解决了我们的问题！
 *
 *注意：此脚本中命令的执行并不是严格意义上的原子性，如果其中第二条指令EXPIRE执行失败，整个脚本执行会返回错误，
 * 但是第一条指令SETNX仍然是已经生效的！不过此种情况基本可以认为是Redis服务器已经崩溃（除非是开发阶段就可以排除的参数错误之类的问题），
 * 那么锁的安全性就已经不是这里可以关注的点了。这里认为对客户端来说是相对原子性的就足够了。
 */
@Service
public class RedisLock {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private  StringRedisTemplate stringRedisTemplate;


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



    private boolean doTryLock(int lockSeconds,String lockKey) throws Exception {
        List<String> keys = Collections.singletonList(lockKey);
        return stringRedisTemplate.execute(SETNX_AND_EXPIRE_SCRIPT, keys,"1", String.valueOf(lockSeconds));
    }

    /**
     * 尝试获得锁，成功返回true，如果失败立即返回false
     *
     * @param lockSeconds 加锁的时间(秒)，超过这个时间后锁会自动释放
     */
    public boolean tryLock(int lockSeconds,String lockKey) {
        try {
            return doTryLock(lockSeconds,lockKey);
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
    public boolean tryLock(final int lockSeconds, final long tryIntervalMillis, final int maxTryCount,String lockKey) {
        int tryCount = 0;
        while (true) {
            if (++tryCount >= maxTryCount) {
                // 获取锁超时
                return false;
            }

            try {
                if (doTryLock(lockSeconds,lockKey)) {
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
     * 如果加锁后的操作比较耗时，调用方其实可以在unlock前根据时间判断下锁是否已经过期
     * 如果已经过期可以不用调用，减少一次请求
     */
    public void unlock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
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
