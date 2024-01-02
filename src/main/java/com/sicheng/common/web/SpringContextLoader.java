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
