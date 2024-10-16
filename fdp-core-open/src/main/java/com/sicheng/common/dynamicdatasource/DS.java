package com.sicheng.common.dynamicdatasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源注解类
 * 本注解的使用方法：通过本注解在service的方法上，指明所用的数据源，其中value为数据源的key值。
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {
    String value();
}
