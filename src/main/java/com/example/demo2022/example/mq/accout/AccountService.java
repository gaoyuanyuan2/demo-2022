package com.example.demo2022.example.mq.accout;

import java.util.concurrent.CompletableFuture;

/**
 * 账户服务
 */
public interface AccountService {
    /**
     * 变更账户金额
     * @param account 账户 ID
     * @param amount 增加的金额，负值为减少
     */
    CompletableFuture<Void> add(int account, int amount);
}
