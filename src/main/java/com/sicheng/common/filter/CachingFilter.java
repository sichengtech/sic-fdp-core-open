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

import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.web.SpringContextHolder;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.*;
import net.sf.ehcache.constructs.web.filter.Filter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;

/**
 * <p>标题: PageCachingFilter </p>
 * <p>描述: 页面高速缓存过滤器
 * 页面缓存主要用Filter过滤器对请求的url进行过滤，如果该url在缓存中出现。那么页面数据就从缓存对象中获取，并以gzip压缩后返回。
 * CachingFilter功能可以对HTTP响应的内容进行缓存。这种方式缓存数据的粒度比较粗，例如缓存整张页面。它的优点是使用简单、效率高。
 * CachingFilter输出的数据会根据浏览器发送的Accept-Encoding头信息进行Gzip压缩。
 * 默认情况下CachingFilter会根据浏览器发送的请求头部所包含的Accept-Encoding参数值来判断是否进行Gzip压缩。
 * <p>
 * 适用场景：给首页做缓存是很必要的
 *
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年10月20日 上午9:51:29
 */
public class CachingFilter extends PatternFilter {

    /**
     * @Fields logger : 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(Filter.class);

    /**
     * @Fields shopCache : 读写缓存的工具类
     */
    private ShopCache shopCache = SpringContextHolder.getBean(ShopCache.class);


    /**
     * @Fields CACHE_TIME : 缓存的有效期，单位秒
     */
    protected int CACHE_TIME = 60 * 30;

    /**
     * <p>描述: init方法 </p>
     *
     * @param filterConfig
     * @throws ServletException
     * @see PatternFilter#innerInit(javax.servlet.FilterConfig)
     */
    @Override
    public void innerInit(FilterConfig filterConfig) throws ServletException {
        // 空就可以了
    }

    /**
     * 生成缓存的key
     * 使用http请求的url做为key
     *
     * @param httpRequest
     * @return
     */
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getMethod());
        stringBuffer.append(httpRequest.getRequestURI());
        if (StringUtils.isNotBlank(httpRequest.getQueryString())) {
            stringBuffer.append("?");
            stringBuffer.append(httpRequest.getQueryString());
        }
        String key = stringBuffer.toString();
        return key;
    }

    /**
     * 总入口
     * 先从缓获取页面的内容，取到了就输出。
     * 取不到再走原业务，并把页面的内容放入缓存，再输出。
     * <p>
     * 已处理cookie、响应头类型等业务，非200状态的页面不会缓存
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     */
    @Override
    public void innerDoFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (response.isCommitted()) {
            throw new AlreadyCommittedException("Response already committed before doing buildPage.");
        }
        logRequestHeaders(request);
        PageInfo pageInfo;
        try {
            //获取页面的内容，先从缓存取，取不到再走原业务。
            pageInfo = buildPageInfo(request, response, filterChain);
            if (pageInfo.isOk()) {
                if (response.isCommitted()) {
                    throw new AlreadyCommittedException("Response already committed after doing buildPage"
                            + " but before writing response from PageInfo.");
                }
                //写出
                writeResponse(request, response, pageInfo);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * 记录请求头信息到日志
     *
     * @param request
     */
    protected void logRequestHeaders(final HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            Map<String, Object> headers = new HashMap<String, Object>();
            Enumeration<String> enumeration = request.getHeaderNames();
            StringBuffer logLine = new StringBuffer();
            logLine.append("Request Headers");
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String headerValue = request.getHeader(name);
                headers.put(name, headerValue);
                logLine.append(": ").append(name).append(" -> ").append(headerValue);
            }
            logger.debug(logLine.toString());
        }
    }

    /**
     * 获取页面的内容，先从缓存取，取不到再走原业务。
     *
     * @param request
     * @param response
     * @param chain
     * @return
     * @throws Exception
     */
    protected PageInfo buildPageInfo(final HttpServletRequest request, final HttpServletResponse response,
                                     final FilterChain chain) throws Exception {
        // 生成缓存的key
        final String key = calculateKey(request);
        PageInfo pageInfo = null;
        try {
            Object element = shopCache.get(key);// 从缓存中取页面内容
            if (element == null) {
                try {
                    // 缓存中没有，走原有业务流程，生成页面内容
                    pageInfo = buildPage(request, response, chain);
                    if (pageInfo.isOk()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("PageInfo ok. Adding to cache , with key " + key);
                        }

                        // 放入缓存
                        shopCache.put(key, pageInfo, pageInfo.getTimeToLiveSeconds());
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("PageInfo was not ok(200). Putting null into cache , with key " + key);
                        }
                        // 清理缓存，非200状态的页面不会缓存
                        shopCache.del(key);
                    }
                } catch (final Throwable throwable) {
                    shopCache.del(key);// 清理缓存
                    throw new Exception(throwable);
                }
            } else {
                // 缓存中有内容，直接返回
                pageInfo = (PageInfo) element;
            }
        } catch (LockTimeoutException e) {
            // do not release the lock, because you never acquired it
            throw e;
        } finally {
            // all done building page, reset the re-entrant flag
            // visitLog.clear();
        }
        return pageInfo;
    }

    /**
     * 走原有的业务流程，并得到响应结果
     *
     * @param request
     * @param response
     * @param chain
     * @return
     * @throws AlreadyGzippedException
     * @throws Exception
     */
    protected PageInfo buildPage(final HttpServletRequest request, final HttpServletResponse response,
                                 final FilterChain chain) throws AlreadyGzippedException, Exception {

        // Invoke the next entity in the chain
        final ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, outstr);
        chain.doFilter(request, wrapper);// 走原有的业务流程
        wrapper.flush();

        long timeToLiveSeconds = CACHE_TIME;// 缓存的有效期，单位秒

        // Return the page info,这是页面内容
        return new PageInfo(wrapper.getStatus(), wrapper.getContentType(), wrapper.getCookies(), outstr.toByteArray(),
                true, timeToLiveSeconds, wrapper.getAllHeaders());
    }

    /**
     * Writes the response from a PageInfo object.
     * <p/>
     * Headers are set last so that there is an opportunity to override
     *
     * @param request
     * @param response
     * @param pageInfo
     * @throws IOException
     * @throws DataFormatException
     * @throws ResponseHeadersNotModifiableException
     */
    protected void writeResponse(final HttpServletRequest request, final HttpServletResponse response,
                                 final PageInfo pageInfo) throws IOException, DataFormatException, ResponseHeadersNotModifiableException {
        boolean requestAcceptsGzipEncoding = acceptsGzipEncoding(request);

        setStatus(response, pageInfo);
        setContentType(response, pageInfo);
        setCookies(pageInfo, response);
        // do headers last so that users can override with their own header sets
        setHeaders(pageInfo, requestAcceptsGzipEncoding, response);
        writeContent(request, response, pageInfo);
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Search robots such as yahoo. While there are quite a few bots, they only hit individual
     * pages once or twice a day. Note that Googlebot as of August 2004 now accepts GZIP.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests are caused by this.
     * <li>Site monitoring tools
     * </ul>
     * As of September 2004, about 34% of requests coming from the Internet did not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }

    /**
     * Checks if request accepts the named encoding.
     */
    protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
        final boolean accepts = headerContains(request, "Accept-Encoding", name);
        return accepts;
    }

    /**
     * Checks if request contains the header value.
     */
    private boolean headerContains(final HttpServletRequest request, final String header, final String value) {
        logRequestHeaders(request);
        final Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the content type.
     *
     * @param response
     * @param pageInfo
     */
    protected void setContentType(final HttpServletResponse response, final PageInfo pageInfo) {
        String contentType = pageInfo.getContentType();
        if (contentType != null && contentType.length() > 0) {
            response.setContentType(contentType);
        }
    }

    /**
     * Set the serializableCookies
     *
     * @param pageInfo
     * @param response
     */
    protected void setCookies(final PageInfo pageInfo, final HttpServletResponse response) {
        final Collection cookies = pageInfo.getSerializableCookies();
        for (Iterator iterator = cookies.iterator(); iterator.hasNext(); ) {
            final Cookie cookie = ((SerializableCookie) iterator.next()).toCookie();
            response.addCookie(cookie);
        }
    }

    /**
     * Status code
     *
     * @param response
     * @param pageInfo
     */
    protected void setStatus(final HttpServletResponse response, final PageInfo pageInfo) {
        response.setStatus(pageInfo.getStatusCode());
    }

    /**
     * Set the headers in the response object, excluding the Gzip header
     *
     * @param pageInfo
     * @param requestAcceptsGzipEncoding
     * @param response
     */
    protected void setHeaders(final PageInfo pageInfo, boolean requestAcceptsGzipEncoding,
                              final HttpServletResponse response) {

        final Collection<Header<? extends Serializable>> headers = pageInfo.getHeaders();

        // Track which headers have been set so all headers of the same name
        // after the first are added
        final TreeSet<String> setHeaders = new TreeSet<String>(
                String.CASE_INSENSITIVE_ORDER);

        for (final Header<? extends Serializable> header : headers) {
            final String name = header.getName();

            switch (header.getType()) {
                case STRING:
                    if (setHeaders.contains(name)) {
                        response.addHeader(name, (String) header.getValue());
                    } else {
                        setHeaders.add(name);
                        response.setHeader(name, (String) header.getValue());
                    }
                    break;
                case DATE:
                    if (setHeaders.contains(name)) {
                        response.addDateHeader(name, (Long) header.getValue());
                    } else {
                        setHeaders.add(name);
                        response.setDateHeader(name, (Long) header.getValue());
                    }
                    break;
                case INT:
                    if (setHeaders.contains(name)) {
                        response.addIntHeader(name, (Integer) header.getValue());
                    } else {
                        setHeaders.add(name);
                        response.setIntHeader(name, (Integer) header.getValue());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("No mapping for Header: " + header);
            }
        }
    }

    /**
     * Writes the response content. This will be gzipped or non gzipped
     * depending on whether the User Agent accepts GZIP encoding.
     * <p/>
     * If the body is written gzipped a gzip header is added.
     *
     * @param response
     * @param pageInfo
     * @throws IOException
     */
    protected void writeContent(final HttpServletRequest request, final HttpServletResponse response,
                                final PageInfo pageInfo) throws IOException, ResponseHeadersNotModifiableException {
        byte[] body;

        boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, pageInfo.getStatusCode());
        if (shouldBodyBeZero) {
            body = new byte[0];
        } else if (acceptsGzipEncoding(request)) {
            body = pageInfo.getGzippedBody();
            if (ResponseUtil.shouldGzippedBodyBeZero(body, request)) {
                body = new byte[0];
            } else {
                ResponseUtil.addGzipHeader(response);
            }
        } else {
            body = pageInfo.getUngzippedBody();
        }
        response.setContentLength(body.length);
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(body);
        out.flush();
    }
}
