package com.kkkoke.gtransaction.intercept;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author KeyCheung
 * @date 2023/10/28
 * @desc
 */
@Configuration
public class GlobalTransactionAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalTransactionRequestInterceptor());
    }
}
