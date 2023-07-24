/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.interceptor;

import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.utils.StringUtils;
import com.sicheng.common.web.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Repage拦截器（带着参数重定向）
 * Repage的意思是：回到原来的页面
 * 有一个列表页，总共有数据50条，我搜索：地区=北京 的数据有10条，我想修改这个10条数据。
 * 我想每修改完一条，页面刷后，再次回到  按条件搜索：地区=北京 的列表，还显示这10条数据，我可继续修改工作
 * <p>
 * 使用方法：
 * 在spring mvc框架的Controller中，当你完成“修改”和“删除”工作后，为了防止表单重复提交，应当重定向到成功页(列表页)。
 * 并且带有repage参数，告诉框架你想“回到原来的页面”。 例如：
 * return "redirect:"+Global.getMemberPath()+"/buy/purchaesOrder/purchaesOrder.htm?repage";
 * 两个要素：必须以redirect:开头，必须带有repage参数。
 * <p>
 * 原理：
 * 使用了spring mvc的Interceptor，在每进入一个Controller的方法时，把request中的全部参数都取出来，放入一个map中，map被放入缓存中。
 * map的key是映射的路径名，值是全部参数串
 * 当发生了重定向，并且带在repage参数，框架会查检重定向的目标，取出重定向的目标的路径做为key，从map中取参数串，如何不为空，就带参数重定向，实现了“回到原来的页面”。
 * <p>
 * 内存保护：
 * map中的元素数量超过10个，就清理。
 *
 * @author 赵磊
 * @version 2016-8-13
 */
public class RepageInterceptor implements HandlerInterceptor {

    String repage = "repage";
    String sessionKey = "REPAGE_PARAMETER";
    String redirect = "redirect:";
    String cacheKey = "repage_";//缓存key的前缀
    long time = 1200;//缓存有效期，单位秒

    @Autowired
    ShopCache cache = SpringContextHolder.getBean(ShopCache.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求的路径
        String path = this.getRequestUrl(request);

        //所有的参数，原样保存为Map<String, String[]>，供后续使用
        Map<String, String[]> param = request.getParameterMap();

        //放入缓存中
        if (StringUtils.isNotBlank(path)) {

            String sessionId = request.getSession().getId();
            String key = cacheKey + sessionId;
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) cache.get(key);

            if (map == null) {
                map = new HashMap<String, Object>();
            }
            boolean update = false;
            if (map.size() > 10) {
                map.clear();//map中的元素数量超过10个，就清理。
                update = true;//标记为已修改，要写入缓存
            }

            if (param != null && param.size() > 0) {
                map.put(path, param);//放入  path 是不带参数的URL /gen/genTable.do，  而不是带参的url：/gen/genTable.do?repage&projectId=19&pageNo=2&pageSize=20
                update = true;//标记为已修改，要写入缓存
            } else {
                Object v = map.remove(path);
                if (v != null) {
                    update = true;//标记为已修改，要写入缓存
                }
            }
            if (update) {
                //map已修改，要写入缓存
                cache.put(key, map, time);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            // 如果null，什么也不做，放过
            return;
        }

        String viewName = modelAndView.getViewName();
        if (viewName != null) {
            viewName = viewName.trim();
        }

        if (!StringUtils.startsWithIgnoreCase(viewName, redirect)) {
            // 如果不是重定向，什么也不做，放过
            return;
        }

        // 去查找viewName的URL中是否带有repage参数
        boolean hasRepage = false;
        int index = viewName.indexOf("?");
        if (index != -1) {
            String queryString = viewName.substring(index + 1);
            String[] arr = queryString.split("&");
            for (String s : arr) {
                if (s != null && !"".equals(s)) {
                    String[] arr2 = s.split("=");
                    if (arr2 != null && arr2.length > 0) {
                        String key = arr2[0];
                        if (key != null && key.equalsIgnoreCase(repage)) {
                            //发现了repage参数
                            hasRepage = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!hasRepage) {
            //未发现repage参数，什么也不做，放过
            return;
        }

        //发现了redirect字样，先截掉redirect字样
        String path = viewName;
        String paramStrCurrentUrl = null;//本次重定向url上带的参数串
        int index1 = path.indexOf(redirect);
        if (index1 != -1) {
            path = path.substring(index1 + redirect.length());
        }
        int index2 = path.indexOf("?");
        if (index2 != -1) {
            paramStrCurrentUrl = path.substring(index2 + 1);//取参数
            path = path.substring(0, index2);//截掉参数，path 是不带参数的URL /gen/genTable.do，而不是带参的url：/gen/genTable.do?repage&projectId=19&pageNo=2&pageSize=20
        }

        String sessionId = request.getSession().getId();
        String key = cacheKey + sessionId;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) cache.get(key);
        if (map != null) {
            Map<String, String[]> mapCacheOldPara = (Map<String, String[]>) map.get(path);
            //做参数合并，同名参数要覆盖，得到合并后的参数串
            Map<String, String[]> mapParamNew = margerParameter(paramStrCurrentUrl, mapCacheOldPara);
            //把Map<String, String[]> 中的参数拼成一个串：a=1&a=2&b=2&c=3
            String allParamStrNew = allParameter2Str(mapParamNew);
            if (allParamStrNew != null) {
                //allParamStrNew再加上repage参数
                allParamStrNew = repage + "&" + allParamStrNew;

                //截取viewName去掉?和?后面的所有参数
                int index3 = viewName.indexOf("?");
                if (index3 != -1) {
                    viewName = viewName.substring(0, index3);
                }
                String joinParam = "";
                if (viewName.indexOf("?") != -1) {
                    if (viewName.endsWith("&")) {
                        joinParam = allParamStrNew;
                    } else {
                        joinParam = "&" + allParamStrNew;
                    }
                } else {
                    joinParam = "?" + allParamStrNew;
                }

                //经过url的匹配，取出了正确的参数,做了参数合并，带着参数重定向
                modelAndView.setViewName(viewName + joinParam);
                //移除,只能使用一次
                Object v = map.remove(path);
                if (v != null) {
                    cache.put(key, map, time);//map已修改，要写入缓存
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

    /**
     * 从request对象中取出请求的URL
     * 兼容Tomcat与WebSphere
     *
     * @param request
     * @return
     */
    public String getRequestUrl(HttpServletRequest request) {
        // 请求的url
        String currentUri = request.getServletPath();// 在tomcat下运行OK
        if (currentUri == null || "".equals(currentUri)) {
            currentUri = request.getPathInfo();// 在WebSphere下运行OK
        }
        return currentUri;
    }

//    /**
//     * 从Request中取出所有参数，原样保存为Map<String, String[]>，供后续使用
//     *
//     * @return Map<String, String [ ]>
//     */
//    @SuppressWarnings("unchecked")
//    public Map<String, String[]> getAllParameterMap(HttpServletRequest request) {
//        Map<String, String[]> map = request.getParameterMap();
//        return map;
//    }

    /**
     * 从Request中取出所有参数，拼成一个串：a=1&a=2&b=2&c=3
     * 注意：a有两个值，分别是1和2，当使用复选框提交数据时会出现这种情况
     * <p>
     * java.net.URLEncoder.encode(v,"utf-8")
     *
     * @return
     */

    /**
     * 做参数合并，同名参数要覆盖
     * 覆盖的方向  paramStr --> mapCacheOldPara
     *
     * @param paramStr 重定向请求带的参数串
     * @param mapCacheOldPara    前一次请求的 全部参数
     * @return 合并后参数，拼成一个串：a=1&a=2&b=2&c=3 返回
     */
    public Map<String, String[]> margerParameter(String paramStr, Map<String, String[]> mapCacheOldPara) {
        if (paramStr != null && mapCacheOldPara != null) {
            //param是ParameterMap类型的，有锁无法修改，要先复制一个普通的Map
            Map<String, String[]> mapParamNew = new LinkedHashMap<>(mapCacheOldPara.size());
            for (String key : mapCacheOldPara.keySet()) {
                String[] obj = (String[]) mapCacheOldPara.get(key);
                mapParamNew.put(key, obj);
            }

            String[] arr = paramStr.split("&");
            for (String str : arr) {
                String[] arr2 = str.split("=");
                if (arr2.length == 2) {//会过滤掉只有参数名的“repage”参数
                    String key = arr2[0];
                    String value = arr2[1];
//                  //覆盖同名参数，覆盖的方向  paramStr --> mapCacheOldPara
//                  //这是重点，我就是需要覆盖同名参数，防止一个参数名出现两次
                    mapParamNew.put(key, new String[]{value});
                }
            }
            return mapParamNew;
        } else {
            return mapCacheOldPara;
        }
    }

    /**
     * 把Map<String, String[]> 中的参数拼成一个串：a=1&a=2&b=2&c=3
     * 注意：a有两个值，分别是1和2，当使用复选框提交数据时会出现这种情况
     * <p>
     * java.net.URLEncoder.encode(v,"utf-8")
     *
     * @return
     */
    private String allParameter2Str(Map<String, String[]> map) {
        StringBuilder sbl = new StringBuilder();
        if (map == null) {
            return null;
        }
        for (String key : map.keySet()) {
            //由于repage是关键字，所以要过滤掉repage
            if (repage.equalsIgnoreCase(key)) {
                continue;
            }

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
}