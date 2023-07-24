/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
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
