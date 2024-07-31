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
package com.sicheng.common.redis;

/**
 * 获取锁的工具类
 * 一把假锁，适用于单机环境
 *
 * @author zhaolei
 */
public class NotLockUtils implements LockManager {

    /**
     * 获取锁
     *
     * @param key 锁的key
     * @return
     */
    public Lock getLock(String key) {
        //一把假锁，适用于单机环境
        Lock lock = new Lock(key);
        return lock;
    }

    /**
     * 获取锁
     * 锁的key来唯一标识一把锁，
     *
     * @param key
     * @param timeout 获取锁等待的超时间，超时后还未获得锁，将得到null。单位：毫秒
     *                0表示不等待，-1表示一直等待（但不会超过锁的默认过期时间是600秒）
     * @return
     */
    public Lock getLock(String key, long timeout) {
        //一把假锁，适用于单机环境
        Lock lock = new Lock(key);
        return lock;
    }

    /**
     * 获取锁
     * 锁的key来唯一标识一把锁，
     *
     * @param key
     * @param timeout     获取锁等待的超时间，超时后还未获得锁，将得到null。单位：毫秒
     *                    0表示不等待，-1表示一直等待（但不会超过锁的过期时间是600秒）
     * @param lockSeconds 锁的过期时间,单位是秒，如果传入null会使用默认值600秒
     * @return
     */
    public Lock getLock(String key, long timeout, Integer lockSeconds) {
        //一把假锁，适用于单机环境
        Lock lock = new Lock(key);
        return lock;
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    public void unLock(Lock lock) {
        //什么也不做
    }
}