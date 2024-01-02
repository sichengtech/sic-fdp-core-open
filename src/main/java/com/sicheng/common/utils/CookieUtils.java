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
package com.sicheng.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

/**
 * Cookie工具类
 *
 * @author zhaolei
 * @version 2013-01-15
 */
public class CookieUtils {

    /**
     * 设置 Cookie（生成时间为1天）
     *
     * @param name  名称
     * @param value 值
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        setCookie(request, response, name, value, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     * @param uri    路径
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path) {
        setCookie(request, response, name, value, path, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）-1代表关闭浏览器就失效
     * @param uri    路径
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        setCookie(request, response, name, value, "/", maxAge);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     * @param uri    路径
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setValue(value);
        response.addCookie(cookie);
    }

    public static void setCookieHttpOnly(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int maxAge) {

        Date date = new Date();
        long ms = date.getTime() + (maxAge * 1000);
        date = new Date(ms);
        response.addHeader("Set-Cookie", name + "=" + (value == null ? "" : value) + ";Path=" + path + "; HttpOnly;Expires=" + date.toString());
    }

    /**
     * 获得指定Cookie的值
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return getCookie(request, null, name, false);
    }

    /**
     * 获得指定Cookie的值，并删除。
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        return getCookie(request, response, name, true);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param name     名字
     * @param isRemove 是否移除
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name, boolean isRemove) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (isRemove) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return value;
    }
}
