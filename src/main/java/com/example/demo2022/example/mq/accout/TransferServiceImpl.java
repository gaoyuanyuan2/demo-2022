package com.example.demo2022.example.mq.accout;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * 转账服务的实现
 */
public class TransferServiceImpl implements TransferService {
    @Inject
    private  AccountService accountService; // 使用依赖注入获取账户服务的实例
    @Override
    public CompletableFuture<Void> transfer(int fromAccount, int toAccount, int amount) {
        // 异步调用 add 方法从 fromAccount 扣减相应金额
        return accountService.add(fromAccount, -1 * amount)
                // 然后调用 add 方法给 toAccount 增加相应金额
                .thenCompose(v -> accountService.add(toAccount, amount));
    }
}
