package com.example.demo2022.example.juc;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * OpenJDK 有个 Loom 项目，就是要解决 Java 语言的轻量级线程问题，在这个项目中，
 * 轻量级线程被叫做Fiber。
 */
public class ThreadPerMessageTest {
    public void test() throws IOException {
        final ServerSocketChannel ssc =
                ServerSocketChannel.open().bind(
                        new InetSocketAddress(8080));
// 处理请求
        try {
            while (true) {
                // 接收请求
                final SocketChannel sc =
                        ssc.accept();
//                Fiber.schedule(() -> {
//                    try {
//                        // 读 Socket
//                        ByteBuffer rb = ByteBuffer
//                                .allocateDirect(1024);
//                        sc.read(rb);
//                        // 模拟处理请求
//                        LockSupport.parkNanos(2000 * 1000000);
//                        // 写 Socket
//                        ByteBuffer wb =
//                                (ByteBuffer) rb.flip()
//                        sc.write(wb);
//                        // 关闭 Socket
//                        sc.close();
//                    } catch (Exception e) {
//                        throw new Exception(e);
//                    }
//                });
            }//while
        } finally {
            ssc.close();
        }
    }
}
