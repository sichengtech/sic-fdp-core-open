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