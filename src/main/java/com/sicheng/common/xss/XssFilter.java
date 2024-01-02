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
package com.sicheng.common.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>标题: XssFilter是一个防止xss攻击的过滤器</p>
 * <p>描述: 、
 * 用户提交所有的数据，都在经过这个过滤器。
 * XssFilter 过滤器 把数据 转交给XssHttpServletRequestWrapper.java处理，转义危险字符。
 * 关键是XssHttpServletRequestWrapper的实现方式，继承servlet的HttpServletRequestWrapper，并重写相应的几个Request“取值”的方法，在取值时对“危险字符”做转义，达到防止xss攻击的目标。
 * 使用XssFilter过滤器，对所有表单提交上来的数据，都会自动处理转义危险字符，存储到数据库的中的数据是无危险字符的安全数据。
 * 主要原理是用到commons-lang3-3.1.jar这个包的org.apache.commons.lang3.StringEscapeUtils.escapeHtml4()这个方法转义危险字符<、>、’、“、&等。
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 *
 * <filter>
 * <filter-name>XssEscape</filter-name>
 * <filter-class>com.sicheng.common.xss.XssFilter</filter-class>
 * </filter>
 * <filter-mapping>
 * <filter-name>XssEscape</filter-name>
 * <url-pattern>/*</url-pattern>
 * <dispatcher>REQUEST</dispatcher>
 * </filter-mapping>
 *
 * @author zhaolei
 * @date 2016年8月1日 上午12:38:33
 */
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest new_request = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(new_request, response);
    }

    @Override
    public void destroy() {
    }
}