/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.dialect.db;

import com.sicheng.common.persistence.dialect.Dialect;

/**
 * Mysql方言的实现
 *
 * @version 1.0 2010-10-10
 * @since JDK 1.5
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
