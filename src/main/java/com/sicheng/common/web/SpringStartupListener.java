/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * <p>标题: Spring启动监听器</p>
 * <p>描述: 在spring容器启动完成后做一些工作</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年4月19日 下午12:58:01
 */
public class SpringStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        //在web项目中（spring mvc），会存在两个spring容器.
        //一个是root application context（父容器） ,另一个是spring mvc的 DispatcherServlet容器（子容器）。
        //就会造成onApplicationEvent方法被执行两次。
        //父容器的parent为null，就用这个来区分两个容器，只在子容器启动完成后做一些工作
        if (evt.getApplicationContext().getParent() != null) {
            setAlwaysUseFullPath(evt.getApplicationContext());
        }
    }

    /**
     * 修改Spring MVC的URL映射控制路径匹配规则，达到满足业务要求
     * 执行setAlwaysUseFullPath(true)
     * <p>
     * Spring MVC的URL映射有一个控制路径匹配的参数alwaysUseFullPath。默认值为false。
     * 当它被设置为false后，总是使用当前servlet映射内的路径来查找Controller。
     * 当它被设置为true后，总是使用当前servlet上下文中的全路径来查找Controller。
     * 我希望，值为true,所以要执行setAlwaysUseFullPath(true)。
     * <p>
     * 当它被设置为false后（默认值）
     * servlet url-pattern= "/*";		request URI= "/test/a" 		映射的Controller= "/test/a"
     * servlet url-pattern= "/";		request URI= "/test/a"		映射的Controller= "/test/a"
     * servlet url-pattern= "/*.do";   request URI= "/test/a.do"	映射的Controller= "/test/a"
     * servlet url-pattern= "/test/*"; request URI= "/test/a"		映射的Controller= "/a"  （这里不满意）
     * <p>
     * 当它被设置为true后
     * servlet url-pattern= "/test/*"; request URI= "/test/a"		映射的Controller= "/test/a" （现在满意了）
     * <p>
     * 如果系统有多个 Spring MVC 的DispatcherServlet容器（子容器），都会被影响
     */
    private void setAlwaysUseFullPath(ApplicationContext applicationContext) {
        RequestMappingHandlerMapping map = applicationContext.getBean(RequestMappingHandlerMapping.class);
        map.setAlwaysUseFullPath(true);
        //值的格式是：org.springframework.web.context.WebApplicationContext:/shop/springServletAdmin，取后半部分
        String name = applicationContext.getId();
        String[] arr = name.split(":");
        if (arr != null && arr.length >= 2) {
            name = arr[1];
        }
        logger.info("Spring Mvc容器(" + name + ")启动完成，并执行setAlwaysUseFullPath(true)");
    }
}
