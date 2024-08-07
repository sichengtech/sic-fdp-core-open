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
package com.sicheng.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * snowflake算法的JAVA版本
 * <p>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，这种时候为了防止ID冲突可以使用36位的UUID，但是UUID有一些缺点，首先他相对比较长，另外UUID一般是无序的。
 * 有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 * 而twitter的snowflake解决了这种需求，最初Twitter把存储系统从MySQL迁移到Cassandra，因为Cassandra没有顺序ID生成机制，所以开发了这样一套全局唯一ID生成服务。
 * <p>
 * Twitter的分布式自增ID算法Snowflake的Java版
 * 在分布式系统中，需要生成全局UID的场合还是比较多的，twitter的snowflake解决了这种需求，实现也还是很简单的，除去配置信息，核心代码就是毫秒级时间41位+机器ID 10位+毫秒内序列12位。
 * 核心代码为其IdWorker这个类实现，其原理结构如下，我分别用一个0表示一位，用—分割开部分的作用：
 * 0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000
 * 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，然后5位datacenter(数据中心)标识位，5位机器ID（并不算标识符，实际是为线程标识），然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * 这样的好处是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和机器ID作区分），并且效率较高，经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 * 默认情况下41bit的时间戳可以支持该算法使用到2082年，10bit的工作机器id可以支持1023台机器，序列号支持1毫秒产生4095个自增序列id。
 * <p>
 * 优化后的开源项目：http://git.oschina.net/yu120/sequence
 * 每秒最多可生产418万个有序的ID，即QPS=400w/s
 *
 * @author zhujuan From: https://github.com/twitter/snowflake An object that
 * generates IDs. This is broken into a separate class in case we ever
 * want to support multiple worker threads per process
 */
public class IdWorker {
    protected static final Logger LOG = LoggerFactory.getLogger(IdWorker.class);

    private long twepoch = 1288834974657L;//时间起点 1970年+41年，做为时间起点（一旦确定不能变动）
    private long workerIdBits = 5L;//机器标识位数
    private long datacenterIdBits = 5L;//数据中心标识位数
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);//机器ID最大值
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);//数据中心ID最大值
    private long sequenceBits = 12L;//毫秒内自增位
    private long workerIdShift = sequenceBits;//机器ID偏左移12位
    private long datacenterIdShift = sequenceBits + workerIdBits;//数据中心ID左移17位

    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;//时间毫秒左移22位
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;//机器ID（实际是为线程标识）
    private long datacenterId;//数据中心ID
    private long sequence = 0L;
    private long lastTimestamp = -1L;/* 上次生产id时间戳 */

    private static IdWorker idWorker;

    /**
     * 生成ID
     * 这是静态方法，请使用这个方法
     *
     * @return
     */
    public static long getId() {
        if (idWorker == null) {
            synchronized (IdWorker.class) {
                if (idWorker == null) {
                    idWorker = new IdWorker();
                }
            }
        }
        return idWorker.nextId();
    }

    /**
     * 构造一个IdWorker
     */
    public IdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);//机器ID
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);//数据中心ID
    }

    /**
     * 构造一个IdWorker
     * 多个IdWorker之间的入参（机器ID和数据中心ID的组合），必须不同
     *
     * @param workerId     机器ID
     * @param datacenterId 数据中心ID
     */
    public IdWorker(long workerId, long datacenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        LOG.info(String.format(
                "worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId));
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            LOG.error(String.format("clock is moving backwards. Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            //当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        //ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    //等待下一个毫秒的到来
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 生成 机器ID
     * 根据java进程的PID来生成
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotEmpty(name)) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 生成数据中心ID
     * 根据MAC地址来生成数据中心ID,每台服务器的mac地址是不变的，所以生成的ID是固定的
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            String macaddr = MacUtils.getMac();
            long macLong = MacUtils.mac2String(macaddr);
            id = macLong % (maxDatacenterId + 1);
        } catch (Exception e) {
            LOG.warn(" getDatacenterId: " + e.getMessage());
        }
        return id;
    }
}