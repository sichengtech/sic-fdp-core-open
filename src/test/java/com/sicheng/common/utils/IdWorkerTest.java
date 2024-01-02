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
package com.sicheng.common.utils;

import org.junit.Test;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>标题: WildcardUtilsTest</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2018年2月17日 下午8:32:02
 */
public class IdWorkerTest {

    /**
     * 生成一个ID
     */
    @Test
    public void test1() {
        Long id = IdWorker.getId();
        System.out.println("IdWorker类生成ID：" + id);
    }

    /**
     * 生成id 100000次
     */
    @Test
    public void test2() {
        Long t1 = System.currentTimeMillis();
        Set<Long> set = new HashSet<Long>();
        int count = 100000;
        for (int i = 0; i < count; i++) {
            Long id = IdWorker.getId();
            set.add(id);
        }
        Long t2 = System.currentTimeMillis();
        System.out.println("生成" + count + "个ID,耗时ms:" + (t2 - t1));
        System.out.println("不重复的ID个数有：" + set.size());
    }

    /**
     * 两个线程，运行10秒，要求生成的ID不重复
     */
    public static void main(String[] args) {
        Set<Long> set = new HashSet<Long>();
        final IdWorker idWorker1 = new IdWorker(0, 0);//多个IdWorker之间的入参（机器ID和数据中心ID的组合），必须不同
        final IdWorker idWorker2 = new IdWorker(1, 31);
        Thread t1 = new Thread(new IdWorkThread(set, idWorker1));
        Thread t2 = new Thread(new IdWorkThread(set, idWorker2));
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        try {
            Thread.sleep(10 * 1000);//运行10秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class IdWorkThread implements Runnable {
        private Set<Long> set;
        private IdWorker idWorker;

        public IdWorkThread(Set<Long> set, IdWorker idWorker) {
            this.set = set;
            this.idWorker = idWorker;
        }

        @Override
        public void run() {
            while (true) {
                long threadId = Thread.currentThread().getId();
                long id = idWorker.nextId();
                if (!set.add(id)) {
                    //发现有重复的id，就输出这个ID。
                    //什么也没输出，就说明生成的ID没有重复，是理想的。
                    System.out.println(threadId + " duplicate:" + id);
                }
            }
        }
    }
}