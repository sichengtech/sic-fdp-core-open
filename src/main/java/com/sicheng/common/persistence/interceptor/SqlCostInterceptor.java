/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.interceptor;

import com.sicheng.common.utils.Reflections;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

/**
 * <p>标题: Sql执行时间记录拦截器</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年9月23日 下午6:12:39
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlCostInterceptor implements Interceptor {
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        /////////////////////
        // 取出记日志的工具类
        /////////////////////
        Object proxy = invocation.getArgs()[0];
        //MetaObject是反射工具
        //MetaObject是Mybatis提供的一个用于方便、优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，
        //同时它支持对JavaBean、Collection、Map三种类型对象的操作。
        MetaObject metaExecutor = MetaObject.forObject(proxy, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        Object object = null;
        // 分离拦截器代理对象链，有可能配置了多个拦截器
        while (metaExecutor.hasGetter("h")) {
            object = metaExecutor.getValue("h");
            metaExecutor = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        if (object == null) {
            //object==null,表示log4j的日志级要求不记录日志
            return invocation.proceed();
        }
        //反射取出记日志的工具类，使用本工具类记录日志的格式与Mybatis原生的一样
        Log log2 = (Log) Reflections.getFieldValue(object, "statementLog");

        long startTime = System.currentTimeMillis();//记录开始时间
        try {
            return invocation.proceed();
        } finally {
            if (log2.isDebugEnabled()) {
                long endTime = System.currentTimeMillis();//记录结束时间
                long sqlCost = endTime - startTime;
                //使用本工具类记录日志的格式与Mybatis原生的一样,看上去是那么的协调
                log2.debug(prefix(false) + "   执行耗时: " + sqlCost + "ms");
            }
        }
    }

    /**
     * 生成表示SQL数据方向的符号
     * 输入符号 ==>
     * 输出符号 <==
     *
     * @param isInput
     * @return
     */
    private String prefix(boolean isInput) {
        int queryStack = 1;
        char[] buffer = new char[queryStack * 2 + 2];
        Arrays.fill(buffer, '=');
        buffer[queryStack * 2 + 1] = ' ';
        if (isInput) {
            buffer[queryStack * 2] = '>';
        } else {
            buffer[0] = '<';
        }
        return new String(buffer);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}