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
package com.sicheng.admin.sys.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import java.util.Map;


/**
 * 日志 Entity 子类，请把你的业务代码写在这里
 *
 * @author fxx
 * @version 2017-02-08
 */
public class Log extends SysLogBase<Log> {

    private static final long serialVersionUID = 1L;

    public Log() {
        super();
    }

    public Log(Long id) {
        super(id);
    }

    // 日志类型（1：接入日志；2：错误日志）
    public static final String TYPE_ACCESS = "1";
    public static final String TYPE_EXCEPTION = "2";

    /**
     * 设置请求参数
     *
     * @param paramMap
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setParams(Map paramMap) {

    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    private User user;

    public User getUser() {
        return null;
    }

    public void setUser(User user) {
        this.user = user;
    }
}