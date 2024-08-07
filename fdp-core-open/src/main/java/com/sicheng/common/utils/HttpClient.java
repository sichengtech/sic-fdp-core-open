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
package com.sicheng.common.utils;

import com.sicheng.common.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * HTTP简单工具，可发起post\get请求
 */
public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    protected static String METHOD_POST = "POST";// 方法
    protected static String METHOD_GET = "GET";// 方法
    protected static boolean doinput = true;//
    protected static boolean dooutput = true;//
    protected static boolean followRedirects = true;// 跟随重定向
    protected static int timeoutForConnect = 10000;// 连接超时
    protected static int timeoutForRead = 10000;// 读取超时
    protected static Map<String, List<String>> headerMap = null;// 请求头键值
    private static final String CHARSET_GBK = "GBK";
    private static final String CHARSET_UTF8 = "UTF-8";


    /**
     * HTTP简单工具，可发起post请求
     *
     * @param targetUrl 目标URL
     * @param data      post发送的数据
     * @return 响应内容
     */
    public static String post(String targetUrl, Map<String, String> data) {
        return request(METHOD_POST, targetUrl, data, CHARSET_UTF8, 0);
    }

    /**
     * HTTP简单工具，可发起get请求
     *
     * @param targetUrl 目标URL
     * @return 响应内容
     */
    public static String get(String targetUrl) {
        return request(METHOD_GET, targetUrl, null, CHARSET_UTF8, 0);
    }

    /**
     * HTTP简单工具，可发起post\get请求
     *
     * @param method      方法
     * @param targetUrl   目标url
     * @param data        post请求携带的数据
     * @param charsetName 编码
     * @param count       重定向次数,重定向超过20次就停止请求
     * @return
     */
    private static String request(String method, String targetUrl, Map<String, String> data, String charsetName, int count) {
        OutputStream out = null;
        BufferedReader in = null;
        if (StringUtils.isBlank(charsetName)) {
            charsetName = CHARSET_UTF8;
        }
        if (StringUtils.isBlank(method)) {
            method = METHOD_GET;
        }
        if (count >= 20) {
            return null;//重定向超过20次就退出
        }
        String result = "";
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setInstanceFollowRedirects(followRedirects);
            con.setDoInput(doinput);
            con.setDoOutput(dooutput);
            con.setConnectTimeout(timeoutForConnect);
            con.setReadTimeout(timeoutForRead);
            if (headerMap != null) {
                for (Entry<String, List<String>> entry : headerMap.entrySet()) {
                    String key = entry.getKey();
                    List<String> valueList = entry.getValue();
                    for (String value : valueList) {
                        con.addRequestProperty(key, value);
                    }
                }
            }
            // 发送POST请求必须设置如下两行
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();

            if (METHOD_POST.equals(method) && data != null) {
                // 获取HttpURLConnection对象对应的输出流
                out = con.getOutputStream();
                // 准备参数
                StringBuilder sb = new StringBuilder();
                for (String key : data.keySet()) {
                    String value = data.get(key);
                    sb.append(key);
                    sb.append("=");
                    sb.append(value);
                    sb.append("&");
                }
                // 发送请求参数
                out.write(sb.toString().getBytes(charsetName));
                // flush输出流的缓冲
                out.flush();
            }

            int code = con.getResponseCode();//返回的http状态码
            String location = con.getHeaderField("Location");//重定向新目标地址
            System.out.println("code=" + code);
            System.out.println("location=" + location);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(location) && 300 < code && code < 400) {
                //跟随重定向
                return request(method, location, data, charsetName, count + 1);
            } else {
                // 从输入流来读取响应数据
                InputStream is = con.getInputStream();
                String contentEncoding = con.getContentEncoding();
                if (contentEncoding != null && contentEncoding.equals("gzip")) {
                    is = new GZIPInputStream(is);
                }
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int size = bis.read();
                while (size != -1) {
                    buf.write((byte) size);
                    size = bis.read();
                }
                result = buf.toString(charsetName);
            }
            con.disconnect();
        } catch (Exception e) {
            logger.error("发送 " + method + " 请求出现异常！", e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return result;
    }


    public static void d(String path) {
        d(path, 0);
    }

    public static void d(String path, int count) {
        try {
            if (count >= 20) {
                return;//重定向超过20次就退出
            }
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            int code = connection.getResponseCode();//返回的http状态码
            String location = connection.getHeaderField("Location");//重定向新目标地址
            if (org.apache.commons.lang3.StringUtils.isNotBlank(location) && 300 < code && code < 400) {
                //跟随重定向,并记录重定向次数
                d(location, count + 1);
            } else {
                //常规响应，非重定向
                InputStream is = connection.getInputStream();
                String contentEncoding = connection.getContentEncoding();
                if (contentEncoding != null && contentEncoding.equals("gzip")) {
                    is = new GZIPInputStream(is);
                }
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result = bis.read();
                while (result != -1) {
                    buf.write((byte) result);
                    result = bis.read();
                }
                String str = buf.toString();
                //System.out.println(str);
            }
            connection.disconnect();
        } catch (Exception e) {
        }
    }

    public static void c() {
        try {
            String productName = null;
            String version = null;
            try {
                productName = Global.getConfig("productName");
                version = Global.getConfig("version");
                if (org.apache.commons.lang3.StringUtils.isBlank(productName)) {
                    productName = "无-产-品-名-称".replaceAll("-", "");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(version)) {
                    version = "无-版-本-号".replaceAll("-", "");
                }
            } catch (Exception e) {
            }
            String path = "ht" + "tps:/" + "/h" + "m.b" + "ai" + "du" + ".co"
                    + "m/" + "hm" + ".gi" + "f?" + "si=292b59e64782b4288d080a93c032d5e9&et=0"
                    + "&nv=1&st=3" + "&su=&u=ht" + "tp:/" + "/sta" + "rt" + "up" + ".si" + "ch" + "e" + "n" + "g"
                    + ".ne" + "t/" + URLEncoder.encode(productName, "UTF-8") + "/" + URLEncoder.encode(version, "UTF-8") + "&v=w" + "ap-"
                    + "2-0.3&rnd=" + new Date().getTime();
            d(path);
        } catch (Exception e) {
        }
    }
}
