/**
 * 本作品使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权。
 * Copyright (c) 2016 SiCheng.Net
 * This software is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
package com.sicheng.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * <p>标题: PatternFilter支持正则表达式的Filter</p>
 * <p>描述: 支持使用正则过滤指定URL
 * web.xml配置时加入init-params: include:配置需要过滤的url规则，支持正则，多个值之间用','分割
 * exclude:配置不需要过滤的url规则，支持正则，多个值之间用','分割
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年10月7日 上午11:58:27
 */
public abstract class PatternFilter implements Filter {
    protected Pattern[] includePattern = null;
    protected Pattern[] excludePattern = null;

    public final void init(FilterConfig filterConfig) throws ServletException {
        String include = filterConfig.getInitParameter("include");
        String exclude = filterConfig.getInitParameter("exclude");
        if (null != include && !"".equals(include)) {
            String[] arr = include.split(",");
            includePattern = new Pattern[arr.length];
            for (int i = 0; i < arr.length; i++) {
                includePattern[i] = Pattern.compile(arr[i]);
            }
        }
        if (null != exclude && !"".equals(exclude)) {
            String[] arr = exclude.split(",");
            excludePattern = new Pattern[arr.length];
            for (int i = 0; i < arr.length; i++) {
                excludePattern[i] = Pattern.compile(arr[i]);
            }
        }
        innerInit(filterConfig);
    }

    /**
     * 子类进行初始化方法
     *
     * @param filterConfig
     */
    public abstract void innerInit(FilterConfig filterConfig) throws ServletException;

    public void destroy() {
        // 空
    }

    /**
     * filter过滤方法，final子类不可覆盖，实现正则匹配规则，子类覆盖innerDoFilter
     */
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String url = ((HttpServletRequest) servletRequest).getRequestURI();
        if (checkExclude(url) || !checkInclude(url)) {// 无需过滤该请求，则pass  
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 调用innerDoFilter进行过滤  
        innerDoFilter(servletRequest, servletResponse, filterChain);
        return;
    }

    /**
     * 需子类覆盖，实现过滤逻辑
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     */
    public abstract void innerDoFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                                       FilterChain filterChain) throws IOException, ServletException;

    /**
     * 检验访问请求是否在include列表中
     *
     * @param requestUrl
     * @return
     */
    public final boolean checkInclude(String requestUrl) {
        boolean flag = true;
        if (null == includePattern || includePattern.length == 0) {
            return flag;
        }
        for (Pattern pat : includePattern) {
            if (flag = pat.matcher(requestUrl).matches())
                break;
        }
        return flag;
    }

    /**
     * 检验访问请求是否在exclude列表中
     *
     * @param requestUrl
     * @return
     */
    public final boolean checkExclude(String requestUrl) {
        boolean flag = false;
        if (null == excludePattern || excludePattern.length == 0) {
            return flag;
        }
        for (Pattern pat : excludePattern) {
            if (flag = pat.matcher(requestUrl).matches())
                break;
        }
        return flag;
    }
}  