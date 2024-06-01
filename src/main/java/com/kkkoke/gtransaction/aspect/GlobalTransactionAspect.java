package com.kkkoke.gtransaction.aspect;

import com.kkkoke.gtransaction.annotation.GlobalTransactional;
import com.kkkoke.gtransaction.transactional.BatchTransaction;
import com.kkkoke.gtransaction.transactional.GlobalTransactionManager;
import com.kkkoke.gtransaction.constant.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author KeyCheung
 * @date 2023/10/28
 * @desc
 */
@Slf4j
@Aspect
@Component
public class GlobalTransactionAspect implements Ordered {

    @Around("@annotation(com.kkkoke.gtransaction.annotation.GlobalTransactional)")
    public void invoke(ProceedingJoinPoint point) {
        // 当前执行的方法上有@GlobalTransactional注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        GlobalTransactional annotation = method.getAnnotation(GlobalTransactional.class);

        // 创建全局分布式事务组得到groupId（表示开启一个全局事务）
        // 或者从线程中拿到groupId（表示已经在一个全局事务中）
        String groupId = "";
        if (annotation.isStart()) {
            groupId = GlobalTransactionManager.createGlobalTransaction();
        } else {
            groupId = GlobalTransactionManager.getCurrentGroupId();
        }

        // 创建一个分支事务
        log.info("create a batch transaction");
        BatchTransaction batchTransaction = GlobalTransactionManager.createBatchTransaction(groupId);

        try {
            // 执行方法（会执行Spring事务逻辑）
            point.proceed();

            // 注册分支事务
            log.info("register a batch transaction-commit");
            GlobalTransactionManager.registerBatchTransaction(batchTransaction, annotation.isEnd(), TransactionStatus.commit);
        } catch (Throwable e) {
            // 注册分支事务
            log.info("register a batch transaction-rollback");
            GlobalTransactionManager.registerBatchTransaction(batchTransaction, annotation.isEnd(), TransactionStatus.rollback);
            log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
