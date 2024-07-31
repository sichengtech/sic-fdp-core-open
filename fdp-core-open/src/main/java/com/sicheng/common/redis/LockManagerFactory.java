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
