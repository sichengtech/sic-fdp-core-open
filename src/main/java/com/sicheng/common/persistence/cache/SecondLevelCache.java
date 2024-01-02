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
