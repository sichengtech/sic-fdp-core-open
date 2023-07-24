/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.shiro;

/**
 * <p>标题: SsoClearAuthorizationCache</p>
 * <p>描述: 本“服务”可以清理sso权限缓存</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cailong
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
