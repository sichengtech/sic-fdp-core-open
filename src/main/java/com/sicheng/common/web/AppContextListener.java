/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
package com.sicheng.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.DriverManager;

/**
 * <p>标题: 取消注册JDBC的驱动</p>
 * <p>描述: AppContextListener类是容器生命周期监听工具类，它的用途在Servlet容器(比如Tomcat)停止时，取消注册JDBC的驱动。</p>
 * <p>
 * 背景：
 * Servlet容器(比如Tomcat)停止时，控制台输出了以下警告信息。只是一个警告并无其它害处。
 * 11-Aug-2022 23:58:52.702 警告 [localhost-startStop-2] org.apache.catalina.loader.WebappClassLoaderBase.clearReferencesJdbc The web application [ROOT] registered the JDBC driver [com.alibaba.druid.proxy.DruidDriver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
 * 11-Aug-2022 23:58:52.702 警告 [localhost-startStop-2] org.apache.catalina.loader.WebappClassLoaderBase.clearReferencesJdbc The web application [ROOT] registered the JDBC driver [com.mysql.cj.jdbc.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
 * <p>
 * 为什么会出现这个警告？
 * Tomcat停止时为了防止内存泄漏，会做一系列检查，发现之前注册的JDBC驱动没有注销，Tomcat就执行了强制注销，并输出了警告信息。
 * 原因：tomcat6最新版本引入内存溢出检测阻止机制，检测到jdbc在tomcat运行时进行注册，但是当tomcat停止时没有解除注册。
 * <p>
 * 解决：
 * 使用AppContextListener类，在Servlet容器(比如Tomcat)停止时，取消注册JDBC的驱动。
 * 在web.xml中加入以下配置
 * <listener>
 * <listener-class>com.sicheng.common.web.AppContextListener</listener-class>
 * </listener>
 * <p>
 * 评语：
 * 这是一个锦上添花的功能，不是一个必要功能。只是在控制台有输出警告信息输出，不好看不美观，并无其它害处。
 * 但我就是看不下去，我是一个追求完美的人，我要解决掉这个警告。
 *
 * 参考文章：
 * https://www.codenong.com/cs106935615/
 * https://blog.csdn.net/ed679ed/article/details/103492378
 *
 * @author zhaolei
 * @date 2022年8月24日
 */
public class AppContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(AppContextListener.class);
    /**
     * Tomcat在停止web应用的时候会调用contextDestroyed方法，执行一些销毁工作
     *
     * @param event
     */
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("Web Server stop.");
        try {
            while (DriverManager.getDrivers().hasMoreElements()) {
                DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
            }
            logger.info("JDBC Driver close.");
            synchronized (AppContextListener.class) {
                try {
                    AppContextListener.class.wait(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            logger.error("",e);
        }
    }

    public void contextInitialized(ServletContextEvent event) {

    }
}