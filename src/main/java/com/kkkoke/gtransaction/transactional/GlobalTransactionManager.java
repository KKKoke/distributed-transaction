package com.kkkoke.gtransaction.transactional;

import com.alibaba.fastjson.JSONObject;
import com.kkkoke.gtransaction.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author KeyCheung
 * @date 2023/10/27
 * @desc
 */
@Component
public class GlobalTransactionManager {

    private static NettyClient nettyClient;

    private static final ThreadLocal<BatchTransaction> currentBatchTransaction = new ThreadLocal<>();

    private static final ThreadLocal<String> currentGroupId = new ThreadLocal<>();

    private static final ThreadLocal<Integer> transactionCount = new ThreadLocal<>();

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        GlobalTransactionManager.nettyClient = nettyClient;
    }

    public static Map<String, BatchTransaction> BATCH_TRANSACTION_MAP = new HashMap<>();

    /**
     * 创建全局分布式事务组，并且返回groupId
     */
    public static String createGlobalTransaction() {
         String groupId = UUID.randomUUID().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", groupId);
        jsonObject.put("command", "create");
        nettyClient.send(jsonObject);
        currentGroupId.set(groupId);
        return groupId;
    }

    /**
     * 创建分支事务
     */
    public static BatchTransaction createBatchTransaction(String groupId) {
        String transactionId = UUID.randomUUID().toString();
        BatchTransaction batchTransaction = new BatchTransaction(groupId, transactionId);
        currentBatchTransaction.set(batchTransaction);
        increaseTransactionCount();
        return batchTransaction;
    }

    /**
     * 注册分支事务
     */
    public static void registerBatchTransaction(BatchTransaction batchTransaction, Boolean isEnd, TransactionStatus transactionStatus) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", batchTransaction.getGroupId());
        jsonObject.put("transactionId", batchTransaction.getTransactionId());
        jsonObject.put("transactionStatus", transactionStatus);
        jsonObject.put("command", "register");
        jsonObject.put("isEnd", isEnd);
        jsonObject.put("transactionCount", GlobalTransactionManager.getTransactionCount());
        nettyClient.send(jsonObject);
    }

    public static BatchTransaction getBatchTransaction(String groupId) {
        return BATCH_TRANSACTION_MAP.get(groupId);
    }

    public static BatchTransaction getCurrentBatchTransaction() {
        return currentBatchTransaction.get();
    }
    public static String getCurrentGroupId() {
        return currentGroupId.get();
    }

    public static void setCurrentGroupId(String groupId) {
        currentGroupId.set(groupId);
    }

    public static Integer getTransactionCount() {
        return transactionCount.get();
    }

    public static void setTransactionCount(int i) {
        transactionCount.set(i);
    }

    public static Integer increaseTransactionCount() {
        int i = Optional.ofNullable(transactionCount.get()).orElse(0) + 1;
        transactionCount.set(i);
        return i;
    }
}
