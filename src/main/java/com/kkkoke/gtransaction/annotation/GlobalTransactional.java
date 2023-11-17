package com.kkkoke.gtransaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author KeyCheung
 * @date 2023/10/27
 * @desc
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalTransactional {

    boolean isStart() default false;

    boolean isEnd() default false;
}
