package com.sicheng.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpPost {
    protected int MAX_RECEIVE_SIZE = 1000 * 1000;//
    protected static String METHOD = "POST";// 方法
    protected static boolean doinput = true;//
    protected static boolean dooutput = true;//
    protected static boolean followRedirects = true;// 跟随重定向
    protected static int timeoutForConnect = 3000;// 连接超时
    protected static int timeoutForRead = 10000;// 读取超时
    protected static Map<String, List<String>> headerMap = null;// 请求头键值
    private static final String URL = "http://open.hc360.com/open/sendSms";// 请求地址
    private static final String CHARSET = "GBK";

    public static void main(String[] args) {

        String client_id = "xxxx";// xx开放平台秘钥id
        String client_secret = "xxxx";// xx开放平台秘钥密码
        String mobile = "13269758170";
        String content = "这是短信内容";
        String time_stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        StringBuilder sbl = new StringBuilder();
        sbl.append("{");
        sbl.append("client_id:'" + client_id + "',");
        sbl.append("client_secret:'" + client_secret + "',");
        sbl.append("mobile:'" + mobile + "',");
        sbl.append("content:'" + content + "',");
        sbl.append("time_stamp:'" + time_stamp + "'");
        sbl.append("}");

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("schema", "json");
        map1.put("param", sbl.toString());

        String str1 = post(URL, map1);
        System.out.println(str1);
    }

    public static String post(String targetUrl, Map<String, String> maps) {
        OutputStream out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(METHOD);
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
            // 获取HttpURLConnection对象对应的输出流
            out = con.getOutputStream();
            // 准备参数
            StringBuilder sb = new StringBuilder();
            for (String key : maps.keySet()) {
                String value = maps.get(key);
                sb.append(key);
                sb.append("=");
                sb.append(value);
                sb.append("&");
            }
            // 发送请求参数
            out.write(sb.toString().getBytes(CHARSET));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
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
                ex.printStackTrace();
            }
        }
        return result;
    }
}
