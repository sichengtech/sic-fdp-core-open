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
package com.sicheng.common.persistence.dialect.db;

import com.sicheng.common.persistence.dialect.Dialect;

/**
 * Mysql方言的实现
 *
 * @version 1.0 2010-10-10
 * 
 */
public class MySQLDialect implements Dialect {

    public boolean supportsLimit() {
        return true;
    }

    /**
     * 将一条普通的业务sql转换为支持分页SQL
     * 这是最常规的分页SQL
     * 单表、多对一、一对一，都使用本分页SQL，使用频率高达90%
     *
     * <pre>
     * 如mysql
     * dialect.pagedSql("select * from user", 12, ":offset",0,":limit") 将返回
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql    业务sql
     * @param offset 开始条数
     * @param limit  每页显示多少纪录条数
     * @return 支持分页查询的sql
     */
    public String pagedSql(String sql, int offset, int limit) {
        StringBuilder stringBuilder = new StringBuilder(sql);
        stringBuilder.append(" limit ");
        if (offset > 0) {
            stringBuilder.append(offset).append(",").append(limit);
        } else {
            stringBuilder.append(limit);
        }
        return stringBuilder.toString();
    }
}
