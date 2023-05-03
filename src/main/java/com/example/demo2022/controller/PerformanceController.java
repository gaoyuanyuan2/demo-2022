package com.example.demo2022.controller;

import com.example.demo2022.service.DeadLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class PerformanceController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello ";
    }

    @GetMapping("/cpu")
    public void cpu() {
        while (true) {

        }
    }

    @GetMapping("/io")
    public void io() throws Exception {
        while (true) {
//            File file = new File("tem/iotest/1.text");
            File file = new File("D:/1.text");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(new byte[1024 * 1024 * 100]);
            outputStream.flush();
            FileInputStream inputStream = new FileInputStream(file);
            byte[] flush = new byte[1024 * 1024 * 100];
            inputStream.read(flush);
            //写出
            outputStream.write(flush, 0, 1024 * 1024 * 10);

            inputStream.close();
            outputStream.close();
            file.delete();
            Thread.sleep(100);
        }
    }

    @GetMapping("/jvm-info")
    public void jvmInfo() {
        List<GarbageCollectorMXBean> garbages = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbage : garbages) {
            System.out.println("垃圾收集器：名称=" + garbage.getName() + ",收集=" + garbage.getCollectionCount() + ",总花费时间="
                    + garbage.getCollectionTime() + ",内存区名称=" + Arrays.deepToString(garbage.getMemoryPoolNames()));
        }
    }

    private static List<int[]> bigObj = new ArrayList<>();

    public static int[] getM() {
        return new int[524288];
    }

    public static char[] generatelChar() {
        return new char[1048576];
    }


    @GetMapping("/jvm")
    public void jvm() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            if (i == 0) {
                Thread.sleep(500L);
                System.out.println("start=[" + new Date() + "]");
            } else {
                Thread.sleep(4000L);
            }
            bigObj.add(getM());
        }
    }

    @GetMapping("/lock")
    public void lock() {
        DeadLock deadLock1 = new DeadLock();
        DeadLock deadLock2 = new DeadLock();

        deadLock1.flag = 0;
        deadLock2.flag = 1;

        Thread thread1 = new Thread(deadLock1);
        Thread thread2 = new Thread(deadLock2);

        thread1.start();
        thread2.start();

    }
}
