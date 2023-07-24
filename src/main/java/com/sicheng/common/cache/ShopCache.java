/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.cache;

import java.util.Set;

/**
 * <p>标题: ShopCache接口</p>
 * <p>ShopCache是一个普通 key\value缓存接口，可满足常规的业务对缓存的需求。
 * ShopCache作为fdp平台的一个组件，提供给业务层面使用。
 * 具体的缓存实现有：1、EhCache，2、Redis
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年6月27日 上午9:41:52
 */
public interface ShopCache {

    /**
     * 放入缓存
     *
     * @param key        键
     * @param value      值
     * @param expireTime 有效期,秒 (如果为0或负数，立即失效)
     * @return 成功：true,失败：false
     */
    public boolean put(Object key, Object value, long expireTime);

    /**
     * 从缓存中取
     *
     * @param key 键
     * @return Object
     */
    public Object get(Object key);

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     *
     * @param pattern 模式，只支持以上几种模式。
     * @return Set<Object>
     */
    public Set<Object> keys(String pattern);

    /**
     * 设定有效期
     *
     * @param key 键
     * @return 成功：true,失败：false
     * @expireTime 有效期, 秒
     */
    public boolean touch(Object key, long expireTime);

    /**
     * 从缓存中取，并再次设定有效期(续命)
     *
     * @param key           键
     * @param newexpireTime 新的有效期
     * @return Object
     */
    public Object getAndTouch(Object key, long newexpireTime);

    /**
     * 删除
     *
     * @param key 键
     * @return 成功：true,失败：false
     */
    public boolean del(Object key);

    /**
     * 删除所有
     */
    public void delAll();

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 存在：true,不存在：false
     */
    public boolean isExists(Object key);
}
