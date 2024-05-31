package com.kkkoke.gtransaction.aspect;

import com.kkkoke.gtransaction.connection.GlobalConnection;
import com.kkkoke.gtransaction.transactional.GlobalTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author KeyCheung
 * @date 2023/10/28
 * @desc
 */
@Aspect
@Component
public class GlobalTransactionDataSourceAspect {

    /**
     * 切的是一个接口，所以所有的实现类都会被切到
     * spring肯定会调用这个方法来生成一个本地事务
     * 所以point.proceed()返回的也是一个Connection
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        // 如果当前线程中存在一个分支事务，那么则创建 GlobalConnection
        if (GlobalTransactionManager.getCurrentBatchTransaction() != null) {
            return new GlobalConnection((Connection) point.proceed(), GlobalTransactionManager.getCurrentBatchTransaction());
        } else {
            return (Connection) point.proceed();
        }
    }
}
