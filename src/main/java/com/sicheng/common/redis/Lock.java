package com.sicheng.common.redis;

import com.sicheng.common.utils.IdGen;
import com.sicheng.common.utils.StringUtils;

import java.io.Closeable;

/**
 * 分布式同步锁
 *
 * @author zhaolei
 */
public final class Lock implements Closeable {

    //设想一下，如果一个请求更新缓存的时间比较长，甚至比锁的有效期还要长，
    //导致在缓存更新过程中，锁就失效了，此时另一个请求会获取锁，但前一个请求在缓存更新完毕的时候，
    //如果不加以判断直接删除锁，就会出现误删除其它请求创建的锁的情况，所以我们在创建锁的时候需要引入一个随机值
    private final String id = IdGen.uuid();//随机数
    private final String key;

    public Lock(String key) {
        this.key = StringUtils.join("LOCK#>", key);
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return this.key;
    }

    /**
     * 释放锁
     *
     * @see java.io.Closeable#close()
     */
    public void close() {
        LockManagerFactory.getLockManager().unLock(this);
    }
}