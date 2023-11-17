package com.kkkoke.gtransaction.config;

import com.kkkoke.gtransaction.aspect.GlobalTransactionAspect;
import com.kkkoke.gtransaction.aspect.GlobalTransactionDataSourceAspect;
import com.kkkoke.gtransaction.netty.NettyClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author KeyCheung
 * @date 2023/11/16
 * @desc
 */
@Configuration
public class GlobalTransactionAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public NettyClient nettyClient() {
        return new NettyClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalTransactionAspect globalTransactionAspect() {
        return new GlobalTransactionAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalTransactionDataSourceAspect globalTransactionDataSourceAspect() {
        return new GlobalTransactionDataSourceAspect();
    }
}
