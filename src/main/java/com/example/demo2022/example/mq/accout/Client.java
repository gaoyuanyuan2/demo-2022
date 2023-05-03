package com.example.demo2022.example.mq.accout;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class Client {
    @Inject
    private TransferService transferService; // 使用依赖注入获取转账服务的实例
    private final static int A = 1000;
    private final static int B = 1001;

    public void syncInvoke() throws ExecutionException, InterruptedException {
        // 同步调用
        transferService.transfer(A, B, 100).get();
        System.out.println(" 转账完成！");
    }

    public void asyncInvoke() {
        // 异步调用
        transferService.transfer(A, B, 100)
                .thenRun(() -> System.out.println(" 转账完成！"));
    }
}