package com.example.demo2022.java.process;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ProcessIdDemo {

    public static void main(String[] args) {

        // Java 9 之前的实现
        getProcessIdBeforeJava9();
    }

    private static void getProcessIdBeforeJava9() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String name = runtimeMXBean.getName();
        String pid = name.substring(0, name.indexOf("@"));
        System.out.println("[Java 9 之前的方法] 当前进程 ID : " + pid);
    }
}
