package com.example.demo2022.example.concurrence;

import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

public class MapTest {

    @Test
    public void test() throws InterruptedException {
        final HashMap<String, String> map = new HashMap<String, String>(2);
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                new Thread(() -> {
                    map.put(UUID.randomUUID().toString(), "");
                }, "ftf" + i).start();
            }
        }, "ftf");
        t.start();
        t.join();
    }
}


