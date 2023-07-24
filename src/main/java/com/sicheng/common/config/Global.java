/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.config;

import com.google.common.collect.Maps;
import com.sicheng.common.utils.PropertiesLoader;
import com.sicheng.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 全局配置类
 * 会自动加载"fdp.properties"核心配置文件
 * 并提供了一批方便读取核心配置文件key\value的工具方法
 *
 * @author zhaolei
 * @version 2014-06-25
 */
public class Global {
    public static final Logger logger = LoggerFactory.getLogger(Global.class);

    /**
     * 核心配置文件文件名
     */
    private static String CORE_CONFIG_FILENAME = "fdp.properties";

//    /**
//     * 当前对象实例
//     */
//    private static Global global = new Global();

    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = Maps.newHashMap();

    /**
     * 属性文件加载对象
     */
    private static PropertiesLoader loader = null;

    /**
     * 显示/隐藏
     */
    public static final String SHOW = "1";
    public static final String HIDE = "0";

    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 对/错
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * 设置properties配置文件的全路径
     * 只用于执行单元测试之前，指定一下测试用的properties配置文件
     * @param coreConfigFilePath
     * @return
     */
    public static void setCoreConfigFilename(String coreConfigFilePath) {
        CORE_CONFIG_FILENAME=coreConfigFilePath;
        loader = new PropertiesLoader(CORE_CONFIG_FILENAME);//重新new一个PropertiesLoader对象。
    }

//    /**
//     * 获取当前对象实例
//     */
//    public static Global getInstance() {
//        return global;
//    }

    /**
     * 获取配置(核心方法最常用)
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            if(loader==null){
                synchronized (Global.class){
                    if(loader==null){
                        loader = new PropertiesLoader(CORE_CONFIG_FILENAME);
                    }
                }
            }

            value = loader.getProperty(key);
            map.put(key, value != null ? value : StringUtils.EMPTY);
        }
        return value;
    }

    /**
     * 获取配置并转为Integer类型(核心方法最常用)
     */
    public static Integer getConfigInteger(String key, Integer defValue) {
        try {
            String value = getConfig(key);
            if (value != null) {
                try {
                    return Integer.valueOf(value);
                } catch (Exception e) {
                    return defValue;
                }
            }
            return defValue;
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 获取admin管理后台根路径，此路径下的controller都需要登录后访问
     */
    public static String getAdminPath() {
        return getConfig("adminPath");
    }

    /**
     * 获取seller商家后台根路径，此路径下的controller都需要登录后访问
     */
    public static String getSellerPath() {
        return getConfig("sellerPath");
    }

    /**
     * 获取member会员中心根路径，此路径下的controller都需要登录后访问
     */
    public static String getMemberPath() {
        return getConfig("memberPath");
    }

    /**
     * 获取front前台根路径，此路径下的controller都可匿名访问，无需登录
     */
    public static String getFrontPath() {
        return getConfig("frontPath");
    }

    /**
     * 获取SSO系统根路径
     */
    public static String getSsoPath() {
        return getConfig("ssoPath");
    }

    /**
     * 获取WAP系统根路径
     */
    public static String getWapPath() {
        return getConfig("wapPath");
    }

    /**
     * 获取upload系统根路径
     */
    public static String getUploadPath() {
        return getConfig("uploadPath");
    }

    /**
     * 获取URL后缀
     */
    public static String getUrlSuffix() {
        return getConfig("urlSuffix");
    }

    /**
     * 获取常量
     *
     * @param field
     * @return
     */
    public static Object getConst(String field) {
        try {
            return Global.class.getField(field).get(null);
        } catch (Exception e) {
            // 异常代表无配置，这里什么也不做
        }
        return null;
    }

    /**
     * 获取工程路径
     *
     * @return
     */
    public static String getProjectPath() {
        // 如果配置了工程路径，则直接返回，否则自动获取。
        String projectPath = Global.getConfig("projectPath");
        if (StringUtils.isNotBlank(projectPath)) {
            return projectPath;
        }
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            if (file != null) {
                while (true) {
                    File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
                    if (f == null || f.exists()) {
                        break;
                    }
                    if (file.getParentFile() != null) {
                        file = file.getParentFile();
                    } else {
                        break;
                    }
                }
                projectPath = file.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPath;
    }
}