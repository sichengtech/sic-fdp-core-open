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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>标题: WildcardUtilsTest</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2018年2月17日 下午8:32:02
 */
@RunWith(SpringJUnit4ClassRunner.class)//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath*:spring-context*.xml"})
public class LockUtilsTest {

    /**
     * 定时任务场景为例
     * 目标：集群环境中，最多只有一个节点执行定时任务，其它节点放弃执行。
     */
    @Test
    public void test1() {
        new Thread(new Task()).start();
        new Thread(new Task()).start();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class Task implements Runnable {
        @Override
        public void run() {
            //集群环境下，多个节点中的定时任务，只有一个能执行
            //下面的第二个入参0表示，第二个及以后来取锁的人，不等待直接取到null
            //这样只有第一个人能取到锁，第二个及以后来取锁的人都取到null，就不做业务，达到了在集中环境中单节点执行任务的目标。
            Lock lock2 = LockManagerFactory.getLockManager().getLock("类名+方法", 0);
            try {
                String tid = "线程ID:" + Thread.currentThread().getId() + ",";
                if (lock2 != null) {
                    //调用业务代码
                    System.out.println(tid + "##调用业务代码##");
                } else {
                    System.out.println(tid + "$$未获得锁，退出执行$$");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //不释放锁，10分钟后会自行过期，考虑到多个节点的操作系统时间可能有误差，这里最多可解决10分钟的误差。
                //定时任务可能3秒就执行完成了，但也要锁住10分钟。一般的定时任务是每天一次、每小时一次，可满足。
                //lock2.close();
            }
        }
    }

    /**
     * 串行执行场景为例
     * 目标：集群环境中，有4个节点，要这4个节点中的程序不能并发执行，要串行，一个接一个的执行。
     */
    @Test
    public void test2() {
        new Thread(new Task2()).start();
        new Thread(new Task2()).start();
        try {
            Thread.sleep(5000L * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class Task2 implements Runnable {
        @Override
        public void run() {
            //集群环境下，多个节点中的任务，串行执行， 谁得到锁谁执行，执行完成后释放锁
            Lock lock = LockManagerFactory.getLockManager().getLock("类名+方法");
            try {
                if (lock != null) {
                    String tid = "线程ID:" + Thread.currentThread().getId() + ",";
                    //调用业务代码
                    System.out.println(tid + "##串行执行，调用业务代码，start##");
                    try {
                        Thread.sleep(5000L); //模拟业务执行耗时
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(tid + "##串行执行，调用业务代码，end##");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.close();//释放锁
            }
        }
    }

}
