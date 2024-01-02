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

import com.sicheng.common.beetl.ShopResourceLoader;
import com.sicheng.common.utils.FileUtils;
import com.sicheng.common.utils.IOUtils;
import com.sicheng.common.utils.MIMEType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * <p>标题: Front系统静态资源访问器</p>
 * <p>描述: Front系统有一套模板，模板可以有包含有静态资源js\css等等，
 * 当在集群环境下，模板被放在tomcat目录以外的一个共享目录中时，如何能正确的访问到静态资源？
 * 本过滤器就可解决这个问题</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年10月16日 下午4:50:28
 */
public class FrontStaticFileFilter extends PatternFilter {
    private String basePath;

    /**
     * <p>描述:  实现父类的抽象方法</p>
     * @param filterConfig filterConfig对象
     * @see com.sicheng.common.filter.PatternFilter#innerInit(FilterConfig)
     */
    @Override
    public void innerInit(FilterConfig filterConfig) throws ServletException {
        this.basePath = filterConfig.getInitParameter("basePath");
    }

    /**
     * <p>描述: 从front模板目录中读取静态资源
     * 由于配置了tomcat集群，front模板目录，可能是一个共享的文件系统目录中，在tomcat之外，所以要单独处理一下，才能读到文件。
     * </p>
     *
     * @param servletRequest request对象
     * @param servletResponse response对象
     * @param filterChain 过滤链
     * @see com.sicheng.common.filter.PatternFilter#innerDoFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void innerDoFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //取得front系统模板文件的存放路径
        //分析这个路径是否在tomcat之外，如果是在tomcat之外，需要干预，使能读取到正确的文件
        String tplChildPath = ShopResourceLoader.getChildPath2();
        String tplParentPath = ShopResourceLoader.getParentPath2();

        //先从子模板目录中查找。
        int code = readFile(tplChildPath, request, response, filterChain);
        if (code == 404) {
            //再从父模板目录中查找。
            code = readFile(tplParentPath, request, response, filterChain);
        }

        //父子模板路径都查找完，仍然未找到。向浏览器输出404。
        if (code == 404) {
            response.setContentType("text/html;charset=UTF-8");//响应头
            response.setStatus(404);
            response.getWriter().write("<html>File not find,404</html>");
        }
    }

    /**
     * @param tplPath     模板路径，可以是父模板路径、子模板路径
     * @param request request对象
     * @param response response对象
     * @param filterChain 过滤链
     * @return 0表示无需处理，404表示需要后续的处理(是否继续向父模板路径中查找。)
     */
    private int readFile(String tplPath, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (tplPath == null) {
            filterChain.doFilter(request, response);
            return 0;
        }
        if (!tplPath.startsWith(ShopResourceLoader.FILE_PATH)) {
            //未以file:开头，说明静态资源文件在tomcat之内，可直接访问到，放过。
            filterChain.doFilter(request, response);
            return 0;
        }

        //访问的静态资源的uri，/views/front/qiche/index.js
        String uri = request.getServletPath();
        if (uri == null || "".equals(uri)) {
            uri = request.getRequestURI();
        }

        //  /views/front/qiche/index.js，截掉/views/*/*/ ，留下index.js
        String filePath = subStringPath(uri, basePath);
        if (filePath == null) {
            filterChain.doFilter(request, response);
            return 0;
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring("/".length());
        }

        String rootPath ;
        if (tplPath != null && tplPath.startsWith(ShopResourceLoader.FILE_PATH)) {
            //从磁盘文件夹加载文件，这个文件夹一般在tomcat之外
            rootPath = tplPath.substring(ShopResourceLoader.FILE_PATH.length());

            StringBuilder sbl = new StringBuilder();
            sbl.append(rootPath);
            if (!(rootPath.endsWith("/") || rootPath.endsWith(File.separator))) {
                sbl.append(File.separator);
            }
            sbl.append(filePath);
            String allPath = sbl.toString();

            File file = new File(allPath);
            if (file.exists()) {
                //文件扩展名
                String suffix = FileUtils.fileSuff(allPath);
                //若文件存在，就直接响应请求
                response.setContentType(MIMEType.getMIMEType(suffix));//响应头
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "public"); //Cache-Control来控制页面的缓存与否,public:浏览器和缓存服务器都可以缓存页面信息；
                response.setHeader("Pragma", "Pragma"); //Pragma:设置页面是否缓存，为Pragma则缓存，no-cache则不缓存
                response.setDateHeader("Expires", new Date().getTime() + (24 * 60 * 60 * 1000)); //Expires:过时期限值
                response.setHeader("Last-Modified", new Date(file.lastModified()).toGMTString());

                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
                IOUtils.copy(in, os);
                IOUtils.closeQuietly(os);//关闭
                IOUtils.closeQuietly(in);//关闭
                return 0;
            } else {
                return 404;//后续业务会根据404判断是否需要后续处理。
            }
        }
        return 0;
    }

    @Override
    public void destroy() {
    }

    /**
     * 按模式来截取路径
     * allPath="/views/front/qiche_def/index.js";
     * basePath="/views/front/   （支持*）
     * 结果：qiche_def/index.js
     *
     * @param allPath  allPath="/views/front/qiche_def/index.js";
     * @param basePath  basePath="/views/front/   （支持*）
     * @return  结果：qiche_def/index.js
     */
    public static String subStringPath(String allPath, String basePath) {
        if (allPath == null || basePath == null) {
            return allPath;
        }

        String aa = allPath;
        String bb = basePath;
        if (aa.startsWith("/")) {
            aa = aa.substring("/".length());
        }
        if (bb.startsWith("/")) {
            bb = bb.substring("/".length());
        }

        String[] arr_aa = aa.split("/");
        String[] arr_bb = bb.split("/");
        if (arr_aa.length == 0) {
            return allPath;
        }
        if (arr_bb.length == 0) {
            return allPath;
        }
        //取最小长度
        int length = arr_aa.length < arr_bb.length ? arr_aa.length : arr_bb.length;

        int index = 0;
        for (int i = 0; i < length; i++) {
            String item_aa = arr_aa[i];
            String item_bb = arr_bb[i];
            if (item_aa.equals(item_bb)) {
                index++;
                continue;
            }
            if ("*".equals(item_bb)) {
                index++;
                continue;
            }

            //未continue，说明未匹配上,要结果
            index = -1;
            break;
        }
        if (index == -1) {
            //未匹配上
            return allPath;
        }
        StringBuilder sbl = new StringBuilder();
        for (int i = index; i < arr_aa.length; i++) {
            sbl.append(arr_aa[i]);
            if (i < arr_aa.length - 1) {
                sbl.append(File.separator);
            }
        }
        return sbl.toString();
    }

    public static void main(String[] a) {
        String allPath = "/views/front/qiche_def/index.js";
        String basePath = "/views/*/*/";
        String ff = subStringPath(allPath, basePath);
        System.out.println(ff);
    }
}
