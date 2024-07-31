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
package com.sicheng.common.web;

import com.sicheng.common.utils.DateUtils;
import com.sicheng.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * R工具--Request\Response便捷工具类
 * <p>
 * 从ThreadLocal中获得Request
 * R工具中，提供了大量的，从Request获取参数并转换为合适的类型的简便方法--是对getParameter()的封装(是最常使用工具方法)
 * 通过getRequest()方法获取Request
 * 通过getResponse()方法获取Response
 * 通过getSession()方法获取session
 * 通过Map<String,String[]> getMapAll()方法获取Request中的全部参数(一个key可以对应多个value)
 * 通过Map<String,String> getMap()方法获取Request中的全部参数（如果一个key对应多个value，只保留第一个value）
 * 如果一个key对应多个value,可以使用 String[] getArray(String paramName) 方法获取
 * R工具中，void setAttr(String key,Object value)--是对setAttribute()的封装
 * R工具中，Object getAttr(String key)--是对getAttribute()的封装
 *
 * @author zhaolei
 * @version 创建时间：2011-4-28 下午04:29:24
 */
public class R implements Filter {
    private static Logger logger = LoggerFactory.getLogger(R.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        R.addRequest(servletRequest);// 放request到当前线程中
        R.addResponse(servletResponse);// 放response到当前线程中
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        R.destroyAll();
    }

    /**
     * 关闭：R工具--Request\Response便捷工具类
     */
    public static void destroyAll() {
        R.removeRequest();
        R.removeResponse();
        logger.info("%%%% 关闭：R工具--Request\\Response便捷工具类 %%%%");
        synchronized (R.class) {
            try {
                R.class.wait(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final static ThreadLocal<HttpServletRequest> request_holder = new NamedThreadLocal<HttpServletRequest>("R工具--Request便捷工具类");
    private final static ThreadLocal<HttpServletResponse> response_holder = new NamedThreadLocal<HttpServletResponse>("R工具--Response便捷工具类");

    private static void addRequest(HttpServletRequest request) {
        request_holder.set(request);
    }

    private static HttpServletRequest getRequestInner() {
        return request_holder.get();
    }

    private static void removeRequest() {
        request_holder.remove();
    }

    private static void addResponse(HttpServletResponse response) {
        response_holder.set(response);
    }

    private static HttpServletResponse getResponseInner() {
        return response_holder.get();
    }

    private static void removeResponse() {
        response_holder.remove();
    }

    /**
     * 获得Request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) (RequestContextHolder.getRequestAttributes());
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            if (request != null) {
                return request;//优先使用spring提供的request
            }
        }
        return getRequestInner();//后使用R工具自己ThreadLocal中的request
    }

    /**
     * 获取Response
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes sra = (ServletRequestAttributes) (RequestContextHolder.getRequestAttributes());
        if (sra != null) {
            HttpServletResponse response = sra.getResponse();
            if (response != null) {
                return response;//优先使用spring提供的response
            }
        }
        return getResponseInner();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 通过Map<String,String[]> getMapAll()方法获取Request中的全部参数(一个key可以对应多个value)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String[]> getMapAll() {
        HttpServletRequest request = getRequest();
        return request.getParameterMap();
    }

    /**
     * 通过Map<String,String> getMap()方法获取Request中的全部参数（如果一个key对应多个value，只保留第一个value）
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> getMap() {
        Map map = getMapAll();
        Map<String, String> newMap = new HashMap<String, String>();
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry element = (Map.Entry) iter.next();
            String strKey = (String) element.getKey();
            String strObj = ((String[]) element.getValue())[0];
            newMap.put(strKey, strObj);
        }
        return newMap;
    }

    /**
     * 如果一个key对应多个value,可以使用 String[] getArray(String paramName) 方法获取
     *
     * @param paramName
     * @return
     */
    public static String[] getArray(String paramName) {
        HttpServletRequest request = getRequest();
        String[] arr = request.getParameterValues(paramName);
        if (arr == null) {
            return new String[0];
        }
        return arr;
    }

    /**
     * 从Request中获取参数的值，并转换为String类型，并trim去掉前后半角空格
     * 是对getParameter()方法的封装，是最常使用工具方法
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中参数值是""，则返回""。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return String类型的参数值
     */
    public static String get(String paramName, String defaultValue) {
        HttpServletRequest request = getRequest();
        String value = request.getParameter(paramName);
        if (value == null) {
            return defaultValue;
        } else {
            return value.trim();
        }
    }

    /**
     * 从Request中获取参数的值，并转换为String类型，并trim去掉前后半角空格
     * 是对getParameter()方法的封装，是最常使用工具方法
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中参数值是""，则返回""。
     *
     * @param paramName 参数名称
     * @return String类型的参数值
     */
    public static String get(String paramName) {
        return get(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Float类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Float类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1.5"，则返回1.5。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Float类型的参数值
     */
    public static Float getFloat(String paramName, Float defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Float类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Float类型是发生异常，则返回null。
     * 如果Request中参数值是"1.5"，则返回1.5。
     *
     * @param paramName 参数名称
     * @return Float类型的参数值
     */
    public static Float getFloat(String paramName) {
        return getFloat(paramName, null);
    }

    /**
     * 从Request中获取参数的值("true","false")，并转换为Boolean类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中参数值是"true"，则返回true。
     * 如果Request中参数值是非"true"，则返回false。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Boolean类型的参数值
     */
    public static Boolean getBoolean(String paramName, Boolean defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        Boolean bl = Boolean.valueOf(temp);
        return bl;
    }

    /**
     * 从Request中获取参数的值("true","false")，并转换为Boolean类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中参数值是"true"，则返回true。
     * 如果Request中参数值是非"true"，则返回false。
     *
     * @param paramName 参数名称
     * @return Boolean类型的参数值
     */
    public static Boolean getBoolean(String paramName) {
        return getBoolean(paramName, null);
    }

    /**
     * 从Request中获取参数的值("1","0",等等int值)，并转换为Boolean类型
     * 如果Request中无此参数(==null)，则返回false
     *
     * @param paramName 参数名称
     * @param _true     指定什么值代表true，若为1表示如果值是1就表示true，其它表示false。
     * @return Boolean类型的参数值
     */
    public static Boolean getBoolean4Int(String paramName, int _true) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return false;
        }
        try {
            Integer ii = Integer.parseInt(temp);
            if (ii == _true) {
                return true;//一般1表示true,0表示false
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 从Request中获取参数的值,如果遇到1就返回true,否则返回false
     * 如果Request中无此参数(==null)，则返回false
     * 。
     *
     * @param paramName 参数名称
     * @return Boolean类型的参数值
     */
    public static Boolean getBoolean4Int(String paramName) {
        return getBoolean4Int(paramName, 1);//1表示，如果遇到1就返回true,否则返回false
    }

    /**
     * 从Request中获取参数的值，并转换为Double类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Double类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1.5"，则返回1.5。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Double类型的参数值
     */
    public static Double getDouble(String paramName, Double defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Double类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Double类型是发生异常，则返回null。
     * 如果Request中参数值是"1.5"，则返回1.5。
     *
     * @param paramName 参数名称
     * @return Double类型的参数值
     */
    public static Double getDouble(String paramName) {
        return getDouble(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Integer类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Integer类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Integer类型的参数值
     */
    public static Integer getInteger(String paramName, Integer defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Integer类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Integer类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName 参数名称
     * @return Integer类型的参数值
     */
    public static Integer getInteger(String paramName) {
        return getInteger(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Long类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Long类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Long类型的参数值
     */
    public static Long getLong(String paramName, Long defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Long类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Long类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName 参数名称
     * @return Long类型的参数值
     */
    public static Long getLong(String paramName) {
        return getLong(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Byte类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Byte类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Byte类型的参数值
     */
    public static Byte getByte(String paramName, Byte defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Byte类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Byte类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName 参数名称
     * @return Byte类型的参数值
     */
    public static Byte getByte(String paramName) {
        return getByte(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Short类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Short类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName    参数名称
     * @param defaultValue 默认值，可传入null
     * @return Short类型的参数值
     */
    public static Short getShort(String paramName, Short defaultValue) {
        HttpServletRequest req = getRequest();
        String temp = req.getParameter(paramName);
        if (temp == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(temp);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从Request中获取参数的值，并转换为Short类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Short类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName 参数名称
     * @return Short类型的参数值
     */
    public static Short getShort(String paramName) {
        return getShort(paramName, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Date类型
     * <p>
     * 如果Request中无此参数(==null)，则返回默认值。
     * 如果Request中有此参数，但在转换为Date类型是发生异常，则返回默认值。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName    参数名称
     * @param format       日期时间的格式，一般如："yyyy-MM-dd HH:mm:ss"
     * @param defaultValue 默认值，可传入null
     * @return Date类型的参数值
     */
    public static Date getDate(String paramName, String format, Date defaultValue) {
        HttpServletRequest req = getRequest();
        String sDateTime = req.getParameter(paramName);
        if (sDateTime == null) {
            return defaultValue;
        }
        Date date = null;
        if ((null != sDateTime) && (0 <= sDateTime.length())) {
            try {
                if(StringUtils.isNotBlank(format)){
                date = (Date) (new SimpleDateFormat(format)).parseObject(sDateTime);
                }else{
                    // 日期型字符串转化为日期的格式，一个一个格式的尝试直到成功
                    // { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
                    // "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
                    // "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
                    date = DateUtils.parseDate(sDateTime);
                }
            } catch (ParseException e) {
                return defaultValue;// 不需要抛出异常
            }
        }
        return date;
    }

    /**
     * 从Request中获取参数的值，并转换为Date类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Date类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * @param paramName 参数名称
     * @param format    日期时间的格式，一般如："yyyy-MM-dd HH:mm:ss"
     * @return Date类型的参数值
     */
    public static Date getDate(String paramName, String format) {
        return getDate(paramName, format, null);
    }

    /**
     * 从Request中获取参数的值，并转换为Date类型
     * <p>
     * 如果Request中无此参数(==null)，则返回null。
     * 如果Request中有此参数，但在转换为Date类型是发生异常，则返回null。
     * 如果Request中参数值是"1"，则返回1。
     *
     * 日期时间的格式，使用了以下多种格式：一个一个格式的尝试直到成功
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     *
     * @param paramName 参数名称
     * @return Date类型的参数值
     */
    public static Date getDate(String paramName) {
        return getDate(paramName, null, null);
    }

    /**
     * R工具中，void setAttr(String key,Object value)--是对setAttribute()的封装
     *
     * @param key
     * @param value
     */
    public static void setAttr(String key, Object value) {
        HttpServletRequest request = getRequest();
        request.setAttribute(key, value);
    }

    /**
     * R工具中，Object getAttr(String key)--是对getAttribute()的封装
     *
     * @param key
     * @return
     */
    public static Object getAttr(String key) {
        HttpServletRequest request = getRequest();
        Object obj = request.getAttribute(key);
        return obj;
    }

    /**
     * 调用request对象的forward方法 ，转发请求
     * HttpServletRequest、HttpServletResponse从当前线程中获取
     *
     * @param path
     * @throws ServletException
     * @throws IOException
     */
    public static void forward(String path) {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用HttpServletResponse的sendRedirect方法，重定向
     *
     * @param url
     * @throws IOException
     */
    public static void redirect(String url) {
        HttpServletResponse response = getResponse();
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//	/**
//	 * 注意：本方法未开发完成
//	 * 从Request中取出所有参数，拼成一个串：{a=[1,2],b=2,c=2}
//	 * 注意：a有两个值，分别是1和2，当使用复选框提交数据时会出现这种情况
//	 *
//	 * @return
//	 */
//	public static String getAllParameterJson() {
//		StringBuilder sbl = new StringBuilder();
//		sbl.append("{");
//		Map<String,String[]> map = getMapAll();
//		int k = 0;
//		for (String key:map.keySet()) {
//			String[] valueArr = map.get(key);
//			sbl.append(key);
//			sbl.append(":");
//			if (valueArr.length == 1) {
//				sbl.append(valueArr[0]);
//			} else {
//				sbl.append("[");
//				for (int i = 0; i < valueArr.length; i++) {
//					String value = valueArr[0];
//					sbl.append(value);
//					if (i < valueArr.length - 1) {
//						sbl.append(",");
//					}
//				}
//				sbl.append("]");
//			}
//
//			if (k < map.keySet().size() - 1) {
//				sbl.append(",");
//			}
//			k++;
//		}
//		sbl.append("}");
//		return sbl.toString();
//	}

    /**
     * 从Request中取出所有参数，拼成一个串：a=1&a=2&b=2&c=3
     * 注意：a有两个值，分别是1和2，当使用复选框提交数据时会出现这种情况
     *
     * @return
     */
    public static String getAllParameter() {
        StringBuilder sbl = new StringBuilder();
        Map<String, String[]> map = getMapAll();
        if (map == null) {
            return null;
        }
        for (String key : map.keySet()) {
            String[] valueArr = map.get(key);//取出一个数组
            for (int i = 0; i < valueArr.length; i++) {
                sbl.append(key);
                sbl.append("=");
                try {
                    String v = valueArr[i];
                    String v2 = java.net.URLEncoder.encode(v, "utf-8");//中文编码的处理
                    sbl.append(v2);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                sbl.append("&");
            }
        }
        if (sbl.length() > 0) {
            return sbl.substring(0, sbl.length() - 1);
        } else {
            return sbl.toString();
        }
    }

    /**
     * 用Response写出html
     *
     * @param html
     * @param encoding
     */
    public static void writeHtml(String html, String encoding) {
        HttpServletResponse response = R.getResponse();
        writeHtml(response, html, encoding);
    }

    /**
     * 用Response写出html
     *
     * @param html
     * @param encoding
     */
    public static void writeHtml(HttpServletResponse response, String html, String encoding) {
        if (html == null) {
            return;
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        try {
            response.setContentType("text/html;charset=" + encoding); //设置编码
            response.setCharacterEncoding(encoding); //设置编码
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PrintWriter out = response.getWriter();
            out.write(html);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用Response写出json
     *
     * @param json
     * @param encoding
     */
    public static void writeJson(String json, String encoding) {
        HttpServletResponse response = R.getResponse();
        writeJson(response, json, encoding);
    }

    /**
     * 用Response写出json
     *
     * @param json
     * @param encoding
     */
    public static void writeJson(HttpServletResponse response, String json, String encoding) {
        if (json == null) {
            return;
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        try {
            response.setContentType("application/json;charset=" + encoding);
            response.setCharacterEncoding(encoding); //设置编码
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PrintWriter out = response.getWriter();
            out.write(json);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取应用上下文的根路径
     *
     * @return
     */
    public static String getCtx() {
        return getRequest().getContextPath();
    }

    /**
     * 获取真实IP地址（获得客户端的公网出口IP）
     * <p>
     * 1、当客户端使用了透明代理时，可以通过本方法获取客户端的公网出口IP。
     * 2、当服务端使用反向代理时，request.getRemoteAddr()取到的是服务端的前端代理的局域网IP,可以通过本方法获取客户端的公网出口IP。
     * 3、当客户端使用了匿名代理时，无法获得客户端的公网出口IP
     * 4、当客户端在它自家的路由器内，无法获得客户端的局域网IP
     * <p>
     * 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，要取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130, 192.168.1.100
     * 用户真实IP为： 192.168.1.110
     *
     * @return
     */
    public static String getRealIp() {
        HttpServletRequest request = getRequest();
        String ipAddress = null;

        //X-Real-IP放在第一个，因为在做反向代理时nginx默认会在请求头加添加X-Real-IP
        //proxy_set_header X-Real-IP $remote_addr;
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-real-ip");
        }

        //X-Forwarded-For放在第二个 （客户端可伪造X-Forwarded-For，不使用它最好）
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }

        //无反向代理时会走这里
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            // "***.***.***.***".length()= 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 根据前缀名，取出一批值，是request.getParameter()的一个便捷工具方法
     * <p>
     * 使用场景：当表单提交上来的数据有统一的前缀（attr_name,attr_image,attr_path）但数量不确定时，可使用本工具方法来取值。
     * 使用示例：Map<String, String> attr = R.getPrameterWithPrefix(request, "attr_",false);
     *
     * @param prefix      前缀
     * @param usefullname key名称的处理，是：表示保留前缀，否：截掉前缀。一般为false
     * @return
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, String> getPrameterWithPrefix(String prefix, boolean usefullname) {
        HttpServletRequest request = getRequest();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        Enumeration<String> names = request.getParameterNames();
        String name, key, value;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name.startsWith(prefix)) {
                key = usefullname ? name : name.substring(prefix.length());
                value = StringUtils.join(request.getParameterValues(name), ',');
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 获取web应用的根目录
     * 支持：Eclipse\IDEA
     * 支持：servlet环境（比如在Tomcat运行环境下）
     * 支持：非servlet环境（比如Main方法 、单元测试、定时任务运行环境）
     * <p>
     * 例如：D:\dev2\eclipse-workspace2\shop-all\shop-web-upload\src\main\webapp\
     *
     * @return
     */
    public static String getWebRoot() {
        String path = getWebRootSub();
        return new File(path).getAbsolutePath() + File.separator;//格式化文件路径

    }

    private static String getWebRootSub() {
        String dir = "/src/main/webapp";//项目目录
        try {

            ////////////////////////////////////////////////////////////////
            //如果是servlet环境，可以取到request,则可从request中取出路径
            ///////////////////////////////////////////////////////////////
            HttpServletRequest request = R.getRequest();
            if (request != null) {
                String path = request.getSession().getServletContext().getRealPath("/");
                logger.debug("getWebRootSub(),path=" + path);

                //Eclipse,勾选 Server modules without publishing
                //D:\dev2\eclipse-workspace2\shop-all\shop-web-wap\target\m2e-wtp\web-resources\ ,去掉了3层，加"/src/main/webapp"
                if (path.endsWith("\\target\\m2e-wtp\\web-resources\\") || path.endsWith("/target/m2e-wtp/web-resources/")) {
                    String tmp = new File(path).getParentFile().getParentFile().getParentFile().getCanonicalPath();
                    return tmp + dir;
                }
                //IDEA
                //C:\dev-java\IdeaProjects\project-shop-cn\shop-all\shop-web-wap\target\wap\ ,不动可直接使用,或去掉2层再加"/src/main/webapp"
                if (path.contains("\\target\\") || path.contains("/target/")) {
                    return path;//不动可直接使用(这样更接近IDEA的行事风格)
                }

                //Eclipse,未勾选 Server modules without publishing
                //D:\dev2\tomcat-8.5.43-1\webapps\shop_shop-web-wap\ ,  不动可直接使用
                return path;
            }

            ////////////////////////////////////////////////////////////////
            //如果是mian方法环境、单元测试环境，从类加载器取路径
            ///////////////////////////////////////////////////////////////

            String path = R.class.getClassLoader().getResource("").toURI().getPath();
            logger.debug("getWebRootSub(),path2=" + path);
            if (path == null) {
                throw new NullPointerException("获取WebRoot异常");
            }

//			IDEA-Main方法
//			C:/dev-java/IdeaProjects/project-shop-cn/fdp-core/fdp-common-utils/target/test-classes/ 去掉了两层，加"/src/main/webapp"
//
//			IDEA-单元测试
//			C:/dev-java/IdeaProjects/project-shop-cn/fdp-core/fdp-common-utils/target/test-classes/ 去掉了两层，加"/src/main/webapp"
//
//			Eclipse-单元测试
//			path4=/D:/dev2/eclipse-workspace2/fdp-core/fdp-common-utils/target/test-classes/  去掉了两层，加"/src/main/webapp"
            if (path.endsWith("\\target\\test-classes\\") || path.endsWith("/target/test-classes/")) {
                String tmp = new File(path).getParentFile().getParentFile().getCanonicalPath();
                return tmp + dir;
            }

//			IDEA-在Tomcat中运行(比如定时任务)
//			path4=/C:/dev-java/IdeaProjects/project-shop-cn/shop-all/shop-web-wap/target/wap/WEB-INF/classes/ 去掉了两层，不加"/src/main/webapp"
            if (path.contains("/target/") && path.endsWith("/WEB-INF/classes/")) {
                return new File(path).getParentFile().getParentFile().getCanonicalPath();
            }

//			Eclipse-Main方法
//			path4=/D:/dev2/eclipse-workspace2/fdp-core/fdp-common-utils/src/main/webapp/WEB-INF/classes/ 去掉了两层，不加"/src/main/webapp"
//
//			Eclipse-勾选 Server modules without publishing
//			在Tomcat中运行
//			path4=/D:/dev2/eclipse-workspace2/shop-all/shop-web-wap/src/main/webapp/WEB-INF/classes/  去掉了两层，不加"/src/main/webapp"
//
//			Eclipse-未勾选 Server modules without publishing
//			在Tomcat中运行
//			path4=/D:/dev2/tomcat-8.5.43-1/webapps/shop_shop-web-wap/WEB-INF/classes/  去掉了两层，加"/src/main/webapp"
            String tmp = new File(path).getParentFile().getParentFile().getCanonicalPath();
            if (tmp.endsWith("\\src\\main\\webapp") || tmp.endsWith("/src/main/webapp")) {
                return tmp;
            } else {
                return tmp + dir;
            }
        } catch (Exception e) {
            throw new RuntimeException("获取WebRoot异常", e);
        }
    }

}