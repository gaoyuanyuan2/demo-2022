package com.example.demo2022.example.juc;

/**
 * 第三方类库在捕获到 Thread.sleep() 方法抛出的中断异常后，没有重新设置线程的中断状态，
 * 那么就会导致线程不能够正常终止。所以强烈建议你设置自己的线程终止标志位，例如在下面的代码中，
 * 使用 isTerminated 作为线程终止标志位，此时无论是否正确处理了线程的中断异常，
 * 都不会影响线程优雅地终止。
 */
public class StopThreadTest2 {
    // 线程终止标志位
    volatile boolean terminated = false;
    boolean started = false;
    // 采集线程
    Thread rptThread;
    // 启动采集功能
    synchronized void start(){
        // 不允许同时启动多个采集线程
        if (started) {
            return;
        }
        started = true;
        terminated = false;
        rptThread = new Thread(()->{
            while (!terminated){
                // 省略采集、回传实现
                report();
                // 每隔两秒钟采集、回传一次数据
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    // 重新设置线程中断状态
                    Thread.currentThread().interrupt();
                }
            }
            // 执行到此处说明线程马上终止
            started = false;
        });
        rptThread.start();
    }

    private void report() {
    }

    // 终止采集功能
    synchronized void stop(){
        // 设置中断标志位
        terminated = true;
        // 中断线程 rptThread
        rptThread.interrupt();
    }
}
