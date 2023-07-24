/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.utils;

import java.security.SecureRandom;
import java.util.UUID;

//import org.activiti.engine.impl.cfg.IdGenerator;

/**
 * 封装各种生成唯一性ID算法的工具类.
 *
 * @author zhaolei
 * @version 2013-01-15
 */
public class IdGen {//implements IdGenerator {

    private static SecureRandom random = new SecureRandom();

    /**
     * 测试方法，4种生成不重复主键的方法
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(IdGen.uuid());//b41e85a17aeb481d9b1b983a52dbb952
        System.out.println(IdGen.snowflake());//821189889077248000
        System.out.println(IdGen.randomLong());//3229101691926935436
        System.out.println(IdGen.randomBase62(3));//Hxc60
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    /**
     * 基于Base62编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Encodes.encodeBase62(randomBytes);
    }

    /**
     * 雪花片算法
     * 18位长，正向增长的数值
     * Twitter的分布式自增ID算法Snowflake的Java版
     *
     * @return
     */
    public static Long snowflake() {
        return IdWorker.getId();
    }

    /**
     * 专为Activiti工作流引擎生成ID的方法
     */
    //@Override
    public String getNextId() {
        //return IdGen.uuid();
        return String.valueOf(IdWorker.getId());
    }


}