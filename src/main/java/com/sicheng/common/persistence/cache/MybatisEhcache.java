/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.cache;

import com.sicheng.common.utils.ObjectUtils;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * <p>标题: Mybatis二级缓存的Ehcache实现 </p>
 * <p>描述: shop使用了Mybatis二级缓存，来提高系统的性能，降低数据库的读取压力。缓存的实现是Ehcache</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年7月5日 下午6:35:47
 */
public final class MybatisEhcache implements SecondLevelCache {

    private static Logger logger = LoggerFactory.getLogger(MybatisEhcache.class);

    private boolean safe = true;//开头，返回的是对象的复本，通过序列化实现的克隆，可防止潜在的修改风险

    /**
     * 当不需要锁时，请使用本虚假锁，来达到无锁的目的
     * As of 3.2.6 MyBatis does not call locking methods.
     */
    private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

    /**
     * 命名空间、缓存id、缓存的分区
     */
    private String id;

    /**
     * 缓存有效期，单位：秒，默认值2天
     */
    private int expire = 172800;

    /**
     * Ehcache的客户端 ，spring静态注入
     */
    private CacheManager cacheManager;


    /**
     * @return the cacheManager
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * @param cacheManager the cacheManager to set
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Ehcache的客户端
     */
    private Ehcache cache;

    /**
     * @Title:构造方法
     * @Description:spring容器需要一个无参的构造方法
     */
    public MybatisEhcache() {
    }

    /**
     * 初始化
     *
     * @param id     设置缓存分区的name
     * @param expire 设置缓存的有效期 （单位：秒）
     */
    public void init(String id, int expire) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.expire = expire;
        this.id = id;
        if (!cacheManager.cacheExists(id)) {
            cacheManager.addCache(id);
        }
        this.cache = cacheManager.getCache(id);
        setTimeToIdleSeconds(expire);//设置缓存的有效期
    }

    /**
     * <p>
     * 清除此缓存实例
     * 清除Redis服务器当前数据库中所有的数据
     * </p>
     *
     * @see org.apache.ibatis.cache.Cache#clear()
     */
    public void clear() {
        long t1 = System.currentTimeMillis();
        cache.removeAll();
        long t2 = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Mybatis二级缓存-ehcache clear,id={},{}ms", id, (t2 - t1));
        }
    }

    /**
     * <p>描述: getId </p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * <p>获取对象 </p>
     *
     * @param key
     * @return
     * @see org.apache.ibatis.cache.Cache#getObject(java.lang.Object)
     */
    public Object getObject(Object key) {
        long t1 = System.currentTimeMillis();
        Element cachedElement = cache.get(CacheKeyUtils.creadkey(key));
        if (cachedElement == null) {
            return null;
        }
        Object v = cachedElement.getObjectValue();
        long t2 = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Mybatis二级缓存-ehcache getObject,key={},{}ms", CacheKeyUtils.creadkey(key), (t2 - t1));
        }
        if (safe) {
            return ObjectUtils.unserialize((byte[]) v);
        } else {
            return v;
        }
    }

    /**
     * <p>
     * 在3.2.6以后，本方法不再被核心调用
     * 缓存所需的任何锁，必须由缓存程序内部自行提供。
     * </p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getReadWriteLock()
     */
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * <p>
     * 可选。这个方法不被core调用
     * 获取当前Redis服务器当前数据库中缓存的key的数量
     * </p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getSize()
     */
    public int getSize() {
        return cache.getSize();
    }

    /**
     * <p>放入对象 </p>
     *
     * @param key
     * @param value
     * @see org.apache.ibatis.cache.Cache#putObject(java.lang.Object, java.lang.Object)
     */
    public void putObject(Object key, Object value) {
        long t1 = System.currentTimeMillis();
        if (safe) {
            value = ObjectUtils.serialize(value);
        }
        cache.put(new Element(CacheKeyUtils.creadkey(key), value));
        long t2 = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Mybatis二级缓存-ehcache putObject,key={},expire={},{}ms", CacheKeyUtils.creadkey(key), expire, (t2 - t1));
        }
    }

    /**
     * <p>
     * 删除对象. 从3.3.0，此方法只在回滚时调用
     * </p>
     *
     * @param key
     * @return
     * @see org.apache.ibatis.cache.Cache#removeObject(java.lang.Object)
     */
    public Object removeObject(Object key) {
        long t1 = System.currentTimeMillis();
        Object obj = getObject(CacheKeyUtils.creadkey(key));
        cache.remove(CacheKeyUtils.creadkey(key));
        long t2 = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Mybatis二级缓存-ehcache removeObject,key={},{}ms", CacheKeyUtils.creadkey(key), (t2 - t1));
        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Cache)) {
            return false;
        }

        Cache otherCache = (Cache) obj;
        return id.equals(otherCache.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EHCache {" + id + "}";
    }

    // DYNAMIC PROPERTIES

    /**
     * Sets the time to idle for an element before it expires. Is only used if the element is not eternal.
     *
     * @param timeToIdleSeconds the default amount of time to live for an element from its last accessed or modified date
     */
    public void setTimeToIdleSeconds(long timeToIdleSeconds) {
        cache.getCacheConfiguration().setTimeToIdleSeconds(timeToIdleSeconds);
    }

    /**
     * Sets the time to idle for an element before it expires. Is only used if the element is not eternal.
     *
     * @param timeToLiveSeconds the default amount of time to live for an element from its creation date
     */
    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        cache.getCacheConfiguration().setTimeToLiveSeconds(timeToLiveSeconds);
    }

    /**
     * Sets the maximum objects to be held in memory (0 = no limit).
     *
     * @param maxElementsInMemory The maximum number of elements in memory, before they are evicted (0 == no limit)
     */
    public void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
        cache.getCacheConfiguration().setMaxEntriesLocalHeap(maxEntriesLocalHeap);
    }

    /**
     * Sets the maximum number elements on Disk. 0 means unlimited.
     *
     * @param maxElementsOnDisk the maximum number of Elements to allow on the disk. 0 means unlimited.
     */
    public void setMaxEntriesLocalDisk(long maxEntriesLocalDisk) {
        cache.getCacheConfiguration().setMaxEntriesLocalDisk(maxEntriesLocalDisk);
    }

    /**
     * Sets the eviction policy. An invalid argument will set it to null.
     *
     * @param memoryStoreEvictionPolicy a String representation of the policy. One of "LRU", "LFU" or "FIFO".
     */
    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        cache.getCacheConfiguration().setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
    }

}