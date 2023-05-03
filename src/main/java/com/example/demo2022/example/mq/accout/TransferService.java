package com.example.demo2022.example.mq.accout;

import java.util.concurrent.CompletableFuture;

/**
 * 转账服务
 */
public interface TransferService {
    /**
     * 异步转账服务
     * @param fromAccount 转出账户
     * @param toAccount 转入账户
     * @param amount 转账金额，单位分
     */
    CompletableFuture<Void> transfer(int fromAccount, int toAccount, int amount);
}