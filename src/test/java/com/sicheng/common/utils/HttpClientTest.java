package com.sicheng.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * <p>标题: xxxx  </p>
 * <p>描述: xxxx  </p>
 * <p>使用示例: </P>
 * <pre>
 *    Window win = new Window(parent);//请作者手动完善此信息
 *    win.show();
 * </pre>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @version 2024-01-01 19:24
 *
 * <p>重要修改历史记录1: xxxx  。修改人：xx</p>
 * <p>重要修改历史记录2: xxxx  。修改人：xx</p>
 */
public class HttpClientTest {

    /**
     * 测试发出get请求
     */
    @Test
    public void test_get() {
        String URL = "http://sicheng.net";// 请求地址 301->https://sicheng.net/   3次
//        String URL = "http://www.sicheng.net";// 请求地址  301->https://www.sicheng.net/ 1次
//        String URL = "https://sicheng.net";// 请求地址  302->http://www.sicheng.net/ 2次
//        String URL = "https://www.sicheng.net";// 请求地址 OK
        String rs = HttpClient.get(URL);
        System.out.println(rs);
    }

    /**
     * 测试发出post请求
     */
    @Test
    public void test_post() {
        String URL = "https://www.jd.com";// 请求地址
        Map<String, String> data = new HashMap<String, String>();
        data.put("schema", "json");
        data.put("param", "param");
        String rs = HttpClient.post(URL, data);
        System.out.println(rs);
    }

}
