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
package com.sicheng.common.persistence.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * <p>标题: Mybatis二级缓存的无缓存实现  </p>
 * <p>描述: shop使用了Mybatis二级缓存，来提高系统的性能，降低数据库的读取压力。缓存的实现是空实现，达到不使用缓存的目的</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2019年10月27日
 */
public final class MybatisNoCache implements SecondLevelCache {

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
     * @Title:构造方法
     * @Description:spring容器需要一个无参的构造方法
     */
    public MybatisNoCache() {
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
        this.id = id;
    }

    /**
     * <p>
     * 清除此缓存实例
     * </p>
     *
     * @see org.apache.ibatis.cache.Cache#clear()
     */
    public void clear() {
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
        return null;
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
        return 0;
    }

    /**
     * <p>放入对象 </p>
     *
     * @param key
     * @param value
     * @see org.apache.ibatis.cache.Cache#putObject(java.lang.Object, java.lang.Object)
     */
    public void putObject(Object key, Object value) {
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
        return null;
    }

}