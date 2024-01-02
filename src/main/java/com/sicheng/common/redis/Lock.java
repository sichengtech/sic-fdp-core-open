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