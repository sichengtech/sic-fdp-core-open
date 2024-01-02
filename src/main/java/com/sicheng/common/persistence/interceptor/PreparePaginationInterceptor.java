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
package com.sicheng.common.persistence.interceptor;

import com.sicheng.common.persistence.Page;
import com.sicheng.common.utils.Reflections;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * Mybatis数据库分页插件，(目前未使用) 拦截StatementHandler的prepare方法
 *
 * @author zhaolei
 * @version 2013-8-28
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PreparePaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 1L;

    public PreparePaginationInterceptor() {
        super();
    }

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            final RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            final BaseStatementHandler delegate = (BaseStatementHandler) Reflections.getFieldValue(statementHandler,
                    DELEGATE);
            final MappedStatement mappedStatement = (MappedStatement) Reflections.getFieldValue(delegate,
                    MAPPED_STATEMENT);

            // //拦截需要分页的SQL
            // if (mappedStatement.getId().matches(_SQL_PATTERN)) {
            // if (StringUtils.indexOfIgnoreCase(mappedStatement.getId(),
            // _SQL_PATTERN) != -1) {
            BoundSql boundSql = delegate.getBoundSql();
            // 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
            Object parameterObject = boundSql.getParameterObject();
            if (parameterObject == null) {
                log.error("参数未实例化");
                throw new NullPointerException("parameterObject尚未实例化！");
            } else {
                final Connection connection = (Connection) ivk.getArgs()[0];
                final String sql = boundSql.getSql();
                // 记录统计
//                final int count = SQLHelper.getCount(sql, connection, mappedStatement, parameterObject, boundSql, log, null, null);
                //将一条普通的业务sql转换为 求count的SQL
                String countSql = DIALECT.countSql(sql);
                //执行一个求count总数的SQL，并返回一个值：总记录数
                int count = SQLHelper.execSQL(countSql, null, mappedStatement, parameterObject, boundSql, log, null, null);


                Page<Object> page = null;
                page = convertParameter(parameterObject, page);
                page.setCount(count);
//                String pagingSql = SQLHelper.generatePageSql(sql, page.getFirstResult(), page.getMaxResults(), DIALECT);
                // 生成常规分页查询SQL,就是最常见的分页SQL,DIALECT可按不同数据库类型可生成专用的SQL
                String pagingSql = DIALECT.pagedSql(sql, page.getFirstResult(), page.getMaxResults());

                if (log.isDebugEnabled()) {
                    log.debug("PAGE SQL:" + pagingSql);
                }
                // 将分页sql语句反射回BoundSql.
                Reflections.setFieldValue(boundSql, "sql", pagingSql);
            }

            if (boundSql.getSql() == null || "".equals(boundSql.getSql())) {
                return null;
            }

        }
        // }
        return ivk.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    /**
     * <p>
     * 设置属性，支持自定义方言类和制定数据库的方式
     * </p>
     *
     * @param properties
     * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties properties) {
        initProperties(properties);
    }
}
