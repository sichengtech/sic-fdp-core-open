/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.dialect.db;

import com.sicheng.common.persistence.dialect.Dialect;

/**
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class DerbyDialect implements Dialect {
    @Override
    public boolean supportsLimit() {
        return false;
    }

    @Override
    public String pagedSql(String sql, int offset, int limit) {
        throw new UnsupportedOperationException("paged queries not supported");
    }


}
