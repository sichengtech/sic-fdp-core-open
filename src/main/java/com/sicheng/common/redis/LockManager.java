package com.sicheng.common.redis;

/**
 * 分布式锁管理接口
 *
 * @author zhaolei
 */
public interface LockManager {

    /**
     * 获取锁
     * 锁的key来唯一标识一把锁，
     * 第一个人获取锁，他会取到锁，（这个锁的过期时间是600秒，程序固化值）
     * 第二个人马上来获取同key锁，会等处于待状态，一直到第一个人释放锁，第二个人才能获得锁。
     * 或达到锁的过期时间是600秒，第二个人才能获得锁。
     * <p>
     * 程序固化了锁的过期时间是600秒，是为了安全。假如因某种原因，业务程序退出，未能发出释放锁的指令，锁将一直存在，后续程序永远无法得到锁。
     * SET 涵盖了 SETEX 的功能，并且 SET 本身已经包含了设置过期时间的功能，set方法的设置锁和设置过期时间是原子操作。
     *
     * @param key 锁的key
     * @return
     */
    public Lock getLock(String key);

    /**
     * 获取锁
     * 锁的key来唯一标识一把锁，
     * 第一个人获取锁，他会取到锁，（这个锁的过期时间是600秒，程序固化值）
     * 第二个人马上来获取同key锁，会等处于待状态，一直到第一个人释放锁，第二个人才能获得锁。
     * 或达到锁的过期时间是600秒，第二个人才能获得锁。
     * 或达到timeout指定的超时时间，第二个人得到null。
     *
     * @param key
     * @param timeout 获取锁等待的超时间，超时后还未获得锁，将得到null。单位：毫秒
     *                0表示不等待，-1表示一直等待（但不会超过锁的默认过期时间是600秒）
     * @return
     */
    public Lock getLock(String key, long timeout);

    /**
     * 获取锁
     * 锁的key来唯一标识一把锁，
     * 第一个人获取锁，他会取到锁，（这个锁的过期时间是600秒，程序固化值）
     * 第二个人马上来获取同key锁，会等处于待状态，一直到第一个人释放锁，第二个人才能获得锁。
     * 或达到锁的过期时间是600秒，第二个人才能获得锁。
     * 或达到timeout指定的超时时间，第二个人得到null。
     *
     * @param key
     * @param timeout     获取锁等待的超时间，超时后还未获得锁，将得到null。单位：毫秒
     *                    0表示不等待，-1表示一直等待（但不会超过锁的过期时间是600秒）
     * @param lockSeconds 锁的过期时间,单位是秒，如果传入null会使用默认值600秒
     * @return
     */
    public Lock getLock(String key, long timeout, Integer lockSeconds);

    /**
     * 释放锁
     *
     * @param lock
     */
    public void unLock(Lock lock);
}