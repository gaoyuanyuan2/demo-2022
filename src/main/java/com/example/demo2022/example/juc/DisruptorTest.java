package com.example.demo2022.example.juc;

import com.couchbase.client.deps.com.lmax.disruptor.RingBuffer;
import com.couchbase.client.deps.com.lmax.disruptor.dsl.Disruptor;
import com.couchbase.client.deps.com.lmax.disruptor.util.DaemonThreadFactory;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 高性能队列Disruptor
 */
public class DisruptorTest {

    @Test
    public void test() throws InterruptedException {
        // 指定 RingBuffer 大小,
        // 必须是 2 的 N 次方
        int bufferSize = 1024;

        // 构建 Disruptor
        Disruptor<LongEvent> disruptor
                = new Disruptor<>(
                LongEvent::new,
                bufferSize,
                DaemonThreadFactory.INSTANCE);

        // 注册事件处理器
        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                System.out.println("E: " + event));

        // 启动 Disruptor
        disruptor.start();

        // 获取 RingBuffer
        RingBuffer<LongEvent> ringBuffer
                = disruptor.getRingBuffer();
        // 生产 Event
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            // 生产者生产消息
            ringBuffer.publishEvent((event, sequence, buffer) ->
                    event.set(buffer.getLong(0)), bb);
            Thread.sleep(1000);
        }
    }
}

// 自定义 Event
class LongEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }
}