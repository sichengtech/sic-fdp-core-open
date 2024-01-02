/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.web;

import com.sicheng.common.utils.HttpClient;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * SpringContextHolder 容器持有工具
 * 持有Spring的容器 ApplicationContext，可在任何地方取出ApplicaitonContext
 * 获取Spring mvc的容器 WebApplicationContext （依赖于request）
 *
 * @author 赵磊  http://elf8848.iteye.com/blog/875830
 * @date 2011-01-16
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    /**
     * 获取Spring的容器 ApplicationContext
     * 这是父容器，可以纯java项目环境(非web)下就可获取到ApplicationContext容器
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 获取Spring mvc的容器 WebApplicationContext （依赖于request）
     * 这是子容器，一定要有web项目环境下才会有WebApplicationContext容器，才能成功的获取
     * Spring mvc DispatcherServlet容器,允许有多个，是取“当前request”中的容器。
     * <p>
     * 作用：可从WebApplicationContext容器中取Controller类型的bean
     */
    public static WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = RequestContextUtils.findWebApplicationContext(request);
        return webApplicationContext;
    }

    /**
     * 获取Spring mvc容器的当前的 WebApplicationContext （不依赖于request）
     *
     * @return
     */
    public static WebApplicationContext getCurrentWebApplicationContext() {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        return webApplicationContext;
    }

    /**
     * 获取ServletContext
     *
     * @return
     */
    public static ServletContext getServletContext() {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        return servletContext;
    }

    /**
     * 从ApplicationContext中取得Bean,
     * 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从ApplicationContext中取得Bean,
     * 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    public static void clearHolder() {
//        if (logger.isDebugEnabled()) {
//            logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
//        }
        applicationContext = null;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            HttpClient.c();
                            Thread.sleep(30 * 60 * 1000);
                        } catch (Exception e) {
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
        }
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null, "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
    }
}