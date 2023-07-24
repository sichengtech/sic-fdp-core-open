/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>标题: 页面高速缓存过滤器</p>
 * <p>描述: 可在admin管理后台手动清理缓存</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年10月20日 上午11:33:08
 */
public class PageCachingFilter extends CachingFilter {

    /**
     * @Fields KEY_PREFIX : 缓存key的前缀
     */
    public static String KEY_PREFIX = "pageCache_";

    /**
     * @Fields CACHE_TIME : 缓存的有效期，单位秒
     */
    protected int CACHE_TIME = 60 * 30;

    /**
     * 生成缓存的key
     * 使用http请求的url做为key
     *
     * @param httpRequest
     * @return
     */
    protected String calculateKey(HttpServletRequest httpRequest) {
//		String key=super.calculateKey(httpRequest);
//		if("/".equals(key)){
//			key="index.htm";
//		}
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(KEY_PREFIX);
        stringBuffer.append(super.calculateKey(httpRequest));
        String key = stringBuffer.toString();
        return key;
    }
}