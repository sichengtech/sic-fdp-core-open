/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.cache;

import com.sicheng.common.web.SpringContextHolder;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * <p>标题: DelegateCache 委托类</p>
 * <p>描述: 通过DelegateCache类，把缓存操作委托给具体的实现类。
 * 目标：通过修改一处spring配置文件切换二级缓存的实现(ehcache\redis),且不用修改100处mybatis的mapper映射文件</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年7月5日 下午12:57:37
 */
public class DelegateCache implements Cache {

    private static Logger logger = LoggerFactory.getLogger(DelegateCache.class);

    //委托目标
    private SecondLevelCache target;
    //缓存分区的name
    private String id;

    //缓存有效时间，每一个缓存分区，使用这一个统一的有效时间。
    //缓存分区内存放着k\v的缓存数据，不能为每一条数据单独设置缓存有效期
    //也不能访问读取时，为缓存数据延长有效期（无法续命）
    private int expire;

    /**
     * @param id
     * @Title:构造方法
     * @Description:
     */
    public DelegateCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        //logger.debug("MyBatis二级缓存 id :" + id);
        this.id = id;
    }

    /**
     * 延迟注入,解决依赖顺序问题
     */
    private SecondLevelCache getTarget() {
        if (target != null) {
            return target;
        } else {
            synchronized (this) {
                if (target != null) {
                    return target;
                }
                //从spring容器中获取 二级缓存的实现类
                //重点：是多实例对象，每一张数据表对应一个对象
                target = SpringContextHolder.getBean(SecondLevelCache.class);
                if (target == null) {
                    logger.warn("未配置Mybatis二级缓存的实现类,系统将不启动二级缓存");
                }
                if (target != null) {
                    //设置缓存分区的name
                    //设置缓存分区的有效期
                    target.init(id, expire);
                }
                return target;
            }
        }
    }

    /**
     * <p>描述: getId() </p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * <p>放入对象 </p>
     *
     * @param key
     * @param value
     * @see org.apache.ibatis.cache.Cache#putObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public void putObject(Object key, Object value) {
        if (getTarget() == null) {
            return;
        }
        getTarget().putObject(key, value);
    }

    /**
     * <p>获取对象</p>
     *
     * @param key
     * @return
     * @see org.apache.ibatis.cache.Cache#getObject(java.lang.Object)
     */
    @Override
    public Object getObject(Object key) {
        if (getTarget() == null) {
            return null;
        }
        return getTarget().getObject(key);
    }

    /**
     * <p>删除对象. 从3.3.0，此方法只在回滚时调用 </p>
     *
     * @param key
     * @return
     * @see org.apache.ibatis.cache.Cache#removeObject(java.lang.Object)
     */
    @Override
    public Object removeObject(Object key) {
        if (getTarget() == null) {
            return null;
        }
        return getTarget().removeObject(key);
    }

    /**
     * <p>清除此缓存实例</p>
     *
     * @see org.apache.ibatis.cache.Cache#clear()
     */
    @Override
    public void clear() {
        if (getTarget() == null) {
            return;
        }
        getTarget().clear();
    }

    /**
     * <p>可选。这个方法不被core调用</p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getSize()
     */
    @Override
    public int getSize() {
        if (getTarget() == null) {
            return 0;
        }
        return getTarget().getSize();
    }

    /**
     * <p>
     * 在3.2.6以后，本方法不再被核心调用
     * 缓存所需的任何锁，必须由缓存程序内部自行提供。 </p>
     *
     * @return
     * @see org.apache.ibatis.cache.Cache#getReadWriteLock()
     */
    @Override
    public ReadWriteLock getReadWriteLock() {
        if (getTarget() == null) {
            return null;
        }
        return getTarget().getReadWriteLock();
    }

    /**
     * 设置缓存的有效期（单位：秒）
     * mybstai会使用 <cache> 标签的<property name="expire" value="172800"/>来调用来方法
     */
    public void setExpire(int expire) {
        this.expire = expire;//要延迟才能正常工作，这是被迫的。
    }
}