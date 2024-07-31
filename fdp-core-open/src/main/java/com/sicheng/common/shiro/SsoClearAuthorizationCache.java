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
package com.sicheng.common.shiro;

/**
 * <p>标题: SsoClearAuthorizationCache</p>
 * <p>描述: 本“服务”可以清理sso权限缓存</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cl
 * @date 2017年9月26日 下午12:32:04
 */
public interface SsoClearAuthorizationCache {

    /**
     * admin模拟登陆(清理单个权限缓存)
     *
     * @return
     */
    public void clearAuthorizationCache(Long uId);

    /**
     * 清理所有权限缓存
     *
     * @return
     */
    public void clearAuthorizationCache();

}
