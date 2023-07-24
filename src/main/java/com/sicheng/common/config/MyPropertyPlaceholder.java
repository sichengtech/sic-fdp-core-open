/*
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */

package com.sicheng.common.config;

import com.sicheng.common.utils.PropertiesLoader;
import com.sicheng.common.utils.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * 自定义的配置文件加载器
 * 支持多环境下加载不同的核心配置文件（fdp.properties）
 * 多环境是指：开发环境、测试环境、生产环境
 */
public class MyPropertyPlaceholder extends PropertyPlaceholderConfigurer {

    /**
     * 方法说明：设置自定义的配置文件文件名,这是一个引导配置文件，用于通过它找到核心配置文件
     *
     * @param bootPropFile
     * @return void
     * @author zhalei
     * @version 2019-12-20 10:16
     */
    public void setCustomPropFile(String bootPropFile) {
        String coreConfigFileName = null;//默认核心配置文件名称
        Properties properties = new Properties();
        //从env.properties文件中查找将要加载的配置文件的名称,key是coreConfigFileName。
        try {
            if (StringUtils.isBlank(bootPropFile)) {
                throw new RuntimeException("加载引导配置文件失败,bootPropFile=null");
            }
            PropertiesLoader prop2 = new PropertiesLoader(bootPropFile);
            properties.putAll(prop2.getProperties());
            String value = prop2.getProperty("coreConfigFileName");//coreConfigFileName是固定的KEY
            if (value != null) {
                coreConfigFileName = value.trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("加载引导配置文件失败:" + bootPropFile, e);
        }

        //加载核心配置文件fdp.properties,根据环境不同会选择不同的fdp.properties文件
        try {
            if (StringUtils.isBlank(coreConfigFileName)) {
                throw new RuntimeException("加载核心配置文件失败,coreConfigFileName=null");
            }
            logger.info("PropertyPlaceholder加载核心配置文件：" + coreConfigFileName);
            PropertiesLoader prop3 = new PropertiesLoader(coreConfigFileName);
            properties.putAll(prop3.getProperties());
        } catch (Exception e) {
            throw new RuntimeException("加载核心配置文件失败:" + coreConfigFileName, e);
        }
        //关键方法,通过这个方法将自定义加载的properties文件加入spring中
        this.setProperties(properties);
    }


}