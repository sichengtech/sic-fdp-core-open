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
package com.sicheng.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>标题: ShopJspFilter</p>
 * <p>描述: 禁止直接访问jsp和html</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author fanxiuxiu
 * @date 2017年8月5日 下午5:40:45
 */
public class ShopJspFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        //"/druid":admin系统的连接池监视
        //"/static/ckfinder/ckfinder.html":admin系统的文件管理
        if ("/index.jsp".equals(req.getServletPath()) || "/index.html".equals(req.getServletPath()) || "/druid".equals(req.getServletPath())) {
            chain.doFilter(request, resp);
        } else {
            try {
                HttpServletResponse response = (HttpServletResponse) resp;
                response.setContentType("text/html;charset=UTF-8"); //设置编码
                response.setCharacterEncoding("UTF-8"); //设置编码
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                PrintWriter out = response.getWriter();
                out.write("禁止访问");
                out.flush();
                out.close();
            } catch (IOException e) {
                //logger.error("向浏览器输出HTML异常", e);
            }
        }
    }

    @Override
    public void destroy() {
    }
}