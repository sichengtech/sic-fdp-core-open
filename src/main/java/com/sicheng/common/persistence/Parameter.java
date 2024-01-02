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
package com.sicheng.common.persistence;

import java.util.HashMap;

/**
 * 查询参数类
 *
 * @author zhaolei
 * @version 2013-8-23
 */
public class Parameter extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /**
     * 构造类，例：new Parameter(id, parentIds)
     *
     * @param values 参数值
     */
    public Parameter(Object... values) {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                put("p" + (i + 1), values[i]);
            }
        }
    }

    /**
     * 构造类，例：new Parameter(new Object[][]{{"id", id}, {"parentIds", parentIds}})
     *
     * @param parameters 参数二维数组
     */
    public Parameter(Object[][] parameters) {
        if (parameters != null) {
            for (Object[] os : parameters) {
                if (os.length == 2) {
                    put((String) os[0], os[1]);
                }
            }
        }
    }

}
