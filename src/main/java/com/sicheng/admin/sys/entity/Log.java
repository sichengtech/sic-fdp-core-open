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

import com.sicheng.admin.sys.dao.UserDao;
import com.sicheng.common.utils.StringUtils;
import com.sicheng.common.web.SpringContextHolder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Map;

//import com.sicheng.admin.store.dao.StoreIndustryDao;
//import com.sicheng.admin.store.entity.Store;
//import com.sicheng.admin.store.entity.StoreIndustry;

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
        if (paramMap == null) {
            return;
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        this.setParams(params.toString());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    private User user;

    public User getUser() {
        if (user == null) {
            UserDao dao = SpringContextHolder.getBean(UserDao.class);
            if (this.getCreateBy() != null) {
                user = dao.selectById(this.getCreateBy().getId());
            }
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}