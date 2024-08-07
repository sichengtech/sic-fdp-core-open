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
package com.sicheng.common.persistence.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * <p>标题: 虚假锁</p>
 * <p>描述: 当不需要锁时，请使用本虚假锁，来达到无锁的目的</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年6月25日 上午7:33:38
 */
class DummyReadWriteLock implements ReadWriteLock {

    private Lock lock = new DummyLock();

    @Override
    public Lock readLock() {
        return lock;
    }

    @Override
    public Lock writeLock() {
        return lock;
    }

    static class DummyLock implements Lock {

        @Override
        public void lock() {
            // Not implemented
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // Not implemented
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
            return true;
        }

        @Override
        public void unlock() {
            // Not implemented
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

}
