/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.filter;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * <p>标题: ShopSiteMeshFilter</p>
 * <p>描述: 对SiteMeshFilter的增强,支持正则表达来过滤url。只让seller和member系统的url走SiteMeshFilter</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年10月7日 上午9:56:50
 */
public class ShopSiteMeshFilter extends PatternFilter {

    private volatile Filter delegate;

    /**
     * <p>描述: 实现父类的抽象方法 </p>
     *
     * @param filterConfig filterConfig对象
     * @throws ServletException
     * @see PatternFilter#innerInit(javax.servlet.FilterConfig)
     */
    @Override
    public void innerInit(FilterConfig filterConfig) throws ServletException {
        delegate = new SiteMeshFilter();//这是原始的过滤器
        delegate.init(filterConfig);
    }

    /**
     * <p>描述: (这里用一句话描述这个方法的作用) </p>
     *
     * @param servletRequest servletRequest对象
     * @param servletResponse servletResponse对象
     * @param filterChain 过滤链
     * @throws IOException
     * @throws ServletException
     * @see PatternFilter#innerDoFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void innerDoFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        delegate.doFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

}
