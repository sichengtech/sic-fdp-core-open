package com.sicheng.common.redis;

/**
 * 通过spring的配置，来配置 定时任务 是否使用分布式锁 来保证单节点执行
 *
 * @author zhaolei
 */
public class LockConfig {
    private boolean distributed = false;//是否使用分布式锁

    public boolean isDistributed() {
        return distributed;
    }

    public void setDistributed(boolean distributed) {
        this.distributed = distributed;
    }


}
