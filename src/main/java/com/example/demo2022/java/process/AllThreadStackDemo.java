package com.example.demo2022.java.process;

import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import java.util.Arrays;

/**
 * 线程信息
 */
public class AllThreadStackDemo {

    public static void main(String[] args) {
        ThreadMXBean threadMXBean = (ThreadMXBean)ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMXBean.getAllThreadIds();
        Arrays.stream(threadIds).forEach(threadId -> {
//            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
//            System.out.println(threadInfo.toString());

            long bytes = threadMXBean. getThreadAllocatedBytes( threadId) ;
            long kBytes = bytes /1024;
            System.out. printf("线程【Id:%d】分配内存%s KB \n", threadId, kBytes);
        });
    }
}
