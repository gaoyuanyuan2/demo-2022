package com.example.demo2022.example.mq;

import com.example.mq.dto.Request;
import com.example.mq.dto.Response;
import com.example.mq.dto.Result;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.Message;
import org.springframework.util.IdGenerator;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息队列实现秒杀
 */
public class RequestHandler {
    // ID 生成器
    @Inject
    private IdGenerator idGenerator;
    // 消息队列生产者
    @Inject
    private Producer producer;
    // 保存秒杀结果的 Map
    @Inject
    private Map<Long, Result> results;

    // 保存 mutex 的 Map
    private Map<Long, Object> mutexes = new ConcurrentHashMap<>();
    // 这个网关实例的 ID
    @Inject
    private long myId;

    @Inject
    private long timeout;

    // 在这里处理 APP 的秒杀请求
    public Response onRequest(Request request) {
        // 获取一个进程内唯一的 UUID 作为请求 id
        Long uuid = idGenerator.generateId().timestamp();
        try {

            Message msg = composeMsg(request, uuid, myId);

            // 生成一个 mutex，用于等待和通知
            Object mutex = new Object();
            mutexes.put(uuid, mutex);
;
            // 发消息
            producer.send((ProducerRecord) msg);

            // 等待后端处理
            synchronized (mutex) {
                mutex.wait(timeout);
            }

            // 查询秒杀结果
            Result result = results.remove(uuid);

            // 检查秒杀结果并返回响应
            if (null != result && result.success()) {
                return Response.success();
            }

        } catch (Throwable ignored) {
        } finally {
            mutexes.remove(uuid);
        }
        // 返回秒杀失败
        return Response.fail();
    }

    private Message composeMsg(Request request, Long uuid, long myId) {
        return null;
    }

    // 在这里处理后端服务返回的秒杀结果
    public void onResult(Result result) {

        Object mutex = mutexes.get(result.uuid());
        if (null != mutex) { // 如果查询不到，说明已经超时了，丢弃 result 即可。
            // 登记秒杀结果
            results.put(result.uuid(), result);
            // 唤醒处理 APP 请求的线程
            synchronized (mutex) {
                mutex.notify();
            }
        }
    }
}
