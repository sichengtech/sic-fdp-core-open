package com.sicheng.common.redis;

import com.sicheng.common.web.SpringContextHolder;

/**
 * 工厂，用于获取LockManager的实现
 *
 * @author zhaolei
 */
public class LockManagerFactory {
    private static LockManager manager = null;

    /**
     * 用于获取LockManager的实现
     * 会根据spring配置文件中的LockConfig bean配置的值，决定使用什么锁（无锁、分布式锁）
     *
     * @return
     */
    public static LockManager getLockManager() {
        if (manager != null) {
            return manager;
        }
        //双重检查
        if (manager == null) {
            synchronized (LockManagerFactory.class) {
                if (manager == null) {
                    LockConfig lockConfig = SpringContextHolder.getBean(LockConfig.class);
                    if (lockConfig != null && lockConfig.isDistributed()) {
                        //是分布式环境
                        String className = "com.sicheng.common.redis.RedisLockUtils";
                        try {
                            Class<?> c = Class.forName(className);
                            manager = (LockManager) c.newInstance();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //是单机环境
                        String className = "com.sicheng.common.redis.NotLockUtils";
                        try {
                            Class<?> c = Class.forName(className);
                            manager = (LockManager) c.newInstance();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return manager;
    }
}
