/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.web;

import com.sicheng.common.utils.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * <p>标题: 启动spring</p>
 * <p>描述: 通过web.xml启动spring容器,并打印出公司信息</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年4月20日 下午5:01:53
 */
public class SpringContextLoader extends org.springframework.web.context.ContextLoaderListener {

    /**
     * <p>通过web.xml启动spring容器 </p>
     *
     * @param servletContext
     * @return
     * @see org.springframework.web.context.ContextLoader#initWebApplicationContext(javax.servlet.ServletContext)
     */
    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        //取web.xml中的<display-name>shop-web-admin</display-name>
        String name = servletContext.getServletContextName();
        if (StringUtils.isBlank(name)) {
            name = servletContext.getContextPath();
        }
        if (StringUtils.isBlank(name)) {
            name = "商城系统";
        }
        printKeyLoadMessage(name);

        return super.initWebApplicationContext(servletContext);
    }

    /**
     * 打印出公司信息
     */
    public static boolean printKeyLoadMessage(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\r\n");
        sb.append("               " + name + " 系统正在启动 - FDP\r\n");
        sb.append("======================================================================\r\n");
        System.out.print(sb.toString());
        return true;
    }
}
