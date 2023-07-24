/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.cache;

import com.sicheng.common.utils.ObjectUtils;
import com.sicheng.common.utils.WildcardUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 标题: EhShopCache
 * </p>
 * <p>
 * 描述: ShopCache接口EhCache实现类
 * </p>
 * <p>
 * 公司: 思程科技 www.sicheng.net
 * </p>
 *
 * @author zhaolei
 * @date 2017年6月26日 上午11:39:17
 */
public class EhShopCache implements ShopCache {

    @Autowired
    private CacheManager cacheManager;

    private Logger logger = LoggerFactory.getLogger(EhShopCache.class);

    private final String SHOP_CACHE = "shopCache";

    private boolean safe = true;//开头，返回的是对象的复本，通过序列化实现的克隆，可防止潜在的修改风险

    /**
     * 获得一个Cache，没有则创建一个。
     *
     * @param cacheName
     * @return
     */
    private Cache getCache(Object cacheName) {
        Cache cache = cacheManager.getCache(cacheName.toString());
        if (cache == null) {
            // 克隆一个ehcache-local.xml中的defaultCache默认缓存
            cacheManager.addCache(cacheName.toString());
            cache = cacheManager.getCache(cacheName.toString());
            // cache.getCacheConfiguration().setEternal(true);//不要永久
        }
        return cache;
    }

    /**
     * 放入缓存
     *
     * @param key        键
     * @param value      值
     * @param expireTime 有效期,秒 (如果为0或负数，立即失效)
     * @return 成功：true,失败：false
     */
    @Override
    public boolean put(Object key, Object value, long expireTime) {
        boolean result = false;
        try {
            if (key != null) {
                Object value2 = null;
                if (safe) {
                    value2 = ObjectUtils.serialize(value);
                }
                Element element = new Element(key, value2);
                element.setTimeToLive((int) expireTime);// 在Element级上控制过期时间
                getCache(SHOP_CACHE).put(element);
                result = true;
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存put出错", e);
        }
        return result;
    }

    /**
     * 从缓存中取
     *
     * @param key 键
     * @return Object
     */
    @Override
    public Object get(Object key) {
        Object result = null;
        try {
            if (key != null) {
                Element element = getCache(SHOP_CACHE).get(key);
                if (element != null) {
                    result = element.getObjectValue();
                }
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存get出错", e);
        }
        Object v = result;
        if (safe) {
            return ObjectUtils.unserialize((byte[]) v);
        } else {
            return v;
        }
    }

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     *
     * @param pattern 模式，只支持以上几种模式。(为了与redis保持一致)
     * @return Set<Object>
     */
    @Override
    public Set<Object> keys(String pattern) {
        try {
            if (StringUtils.isBlank(pattern)) {
                return new HashSet<Object>(0);//为了与redis保持一致
            }
            @SuppressWarnings("unchecked")
            List<Object> element = getCache(SHOP_CACHE).getKeys();
            if (pattern != null && "*".equals(pattern.trim())) {
                return new HashSet<Object>(element);
            }

            if (StringUtils.isNotBlank(pattern)) {
                Set<Object> arr = new HashSet<Object>();
                for (Object obj : element) {
                    String key = (String) obj;
                    if (WildcardUtils.test(pattern, key)) {
                        arr.add(key);
                    }
                }
                return arr;
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存keys出错", e);
        }
        return null;
    }

    /**
     * 设定有效期
     *
     * @param key 键
     * @return 成功：true,失败：false
     * @expireTime 有效期, 秒 (如果为0或负数，立即失效)
     */
    @Override
    public boolean touch(Object key, long expireTime) {
        boolean result = false;
        try {
            if (key != null) {
                Element element = getCache(SHOP_CACHE).get(key);
                element.setTimeToLive((int) expireTime);// 在Element级上控制过期时间
                getCache(SHOP_CACHE).put(element);
                result = true;
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存get出错", e);
        }
        return result;
    }

    /**
     * 从缓存中取，并再次设定有效期(续命)
     *
     * @param key           键
     * @param newexpireTime 新的有效期
     * @return Object
     */
    @Override
    public Object getAndTouch(Object key, long newexpireTime) {
        Object result = null;
        try {
            if (key != null) {
                Element element = getCache(SHOP_CACHE).get(key);
                if (element != null) {
                    result = element.getObjectValue();
                    element.setTimeToLive((int) newexpireTime);// 在Element级上控制过期时间
                    getCache(SHOP_CACHE).put(element);
                }
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存get出错", e);
        }
        Object v = result;
        if (safe) {
            return ObjectUtils.unserialize((byte[]) v);
        } else {
            return v;
        }
    }

    /**
     * 删除
     *
     * @param key 键
     * @return 成功：true,失败：false
     */
    @Override
    public boolean del(Object key) {
        boolean result = false;
        try {
            if (key != null) {
                getCache(SHOP_CACHE).remove(key);
                result = true;
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存del出错", e);
        }
        return result;
    }

    /**
     * 删除所有
     */
    @Override
    public void delAll() {
        try {
            getCache(SHOP_CACHE).removeAll();
        } catch (Exception e) {
            logger.debug("EhCache缓存delAll出错", e);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 存在：true,不存在：false
     */
    @Override
    public boolean isExists(Object key) {
        try {
            if (key != null) {
                // getQuiet 从缓存中取元素，但不更新元素的状态。就是不touch不续命
                // 续命是指：timeToIdleSeconds参数表示的，若元素xxx秒未被访问就会过期，一访问就会延长有效期。
                // 这里未使用isKeyInCache方法,因为它只判断key是否存在，并不判断这个缓存元素过期没有。
                Element element = getCache(SHOP_CACHE).getQuiet(key);
                if (element != null) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.debug("EhCache缓存exists出错", e);
        }
        return false;
    }

}