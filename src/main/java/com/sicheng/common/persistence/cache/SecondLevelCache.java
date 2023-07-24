/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.cache;

import org.apache.ibatis.cache.Cache;

/**
 * <p>标题: SecondLevelCache</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年7月5日 下午2:55:22
 */
public interface SecondLevelCache extends Cache {

    /**
     * 设置缓存分区的name
     */
    //void setId(String id);

    /**
     * 初始化
     *
     * @param id     设置缓存分区的name
     * @param expire 设置缓存的有效期
     */
    void init(String id, int expire);

//	/**
//	 * 设置缓存的有效期
//	 */
//	void setExpire(int expire);
}
