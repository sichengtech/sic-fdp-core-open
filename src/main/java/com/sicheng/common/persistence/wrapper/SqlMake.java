/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.wrapper;

import com.sicheng.common.utils.StringUtils;

/**
 * <p>标题: SqlMake</p>
 * <p>描述: WHERE条件自定义拼接的工具</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年1月31日 下午9:52:33
 */
public class SqlMake {

    protected static final String AND = " AND ";
    protected static final String OR = " OR ";
    protected static final String AND_NEW = ") \nAND (";
    protected static final String OR_NEW = ") \nOR (";

    /**
     * SqlConditionGroup SQL条件组
     */
    private SqlConditionGroup sqlConditionGroup = new SqlConditionGroup();

    public SqlMake WHERE(String condition, Object... params) {
        if (condition == null) {
            throw new WrapperException("Value for conditions cannot be null");
        }
        SqlConditionElement element = new SqlConditionElement(condition, params);
        SqlSafe.securityCheck(element);// 安全检查 ,未通过检查则会抛出异常
        getSqlConditionGroup().whereList.add(element);
        getSqlConditionGroup().lastList = getSqlConditionGroup().whereList;
        return this;
    }

    public SqlMake OR() {
        getSqlConditionGroup().lastList.add(OR);
        getSqlConditionGroup().setFirstRelationOperator(OR);
        return this;
    }

    public SqlMake OR_NEW() {
        getSqlConditionGroup().lastList.add(OR_NEW);
        getSqlConditionGroup().setFirstRelationOperator(OR_NEW);
        return this;
    }

    public SqlMake AND() {
        getSqlConditionGroup().lastList.add(AND);
        getSqlConditionGroup().setFirstRelationOperator(AND);
        return this;
    }

    public SqlMake AND_NEW() {
        getSqlConditionGroup().lastList.add(AND_NEW);
        getSqlConditionGroup().setFirstRelationOperator(AND_NEW);
        return this;
    }

    public SqlMake GROUP_BY(String columns) {
        SqlSafe.securityCheck(new SqlConditionElement(columns));// 安全检查 ,未通过检查则会抛出异常
        getSqlConditionGroup().groupByList.add(columns);
        return this;
    }

    public SqlMake HAVING(String condition, Object... params) {
        if (condition == null) {
            throw new WrapperException("Value for conditions cannot be null");
        }
        SqlConditionElement element = new SqlConditionElement(condition, params);
        SqlSafe.securityCheck(element);// 安全检查 ,未通过检查则会抛出异常
        getSqlConditionGroup().havingList.add(element);
        getSqlConditionGroup().lastList = getSqlConditionGroup().havingList;
        return this;
    }

    public SqlMake ORDER_BY(String columns) {
        SqlSafe.securityCheck(new SqlConditionElement(columns));// 安全检查 ,未通过检查则会抛出异常
        getSqlConditionGroup().orderByList.add(columns);
        return this;
    }

    public SqlConditionGroup getSqlConditionGroup() {
        return sqlConditionGroup;
    }

    public String getOutputSqlAll() {
        return getSqlConditionGroup().getOutputSqlAll();
    }

    public String getOutputSqlWhere() {
        return getSqlConditionGroup().getOutputSqlWhere();
    }

    public String getOutputSqlOrderBy() {
        return getSqlConditionGroup().getOutputSqlOrderBy();
    }

    /**
     * 将EXISTS语句添加到WHERE条件中
     *
     * @param value
     * @return
     */
    public SqlMake EXISTS(String value) {
        handerExists(value, false);
        return this;
    }

    /**
     * 处理EXISTS操作
     *
     * @param value
     * @param isNot 是否为NOT EXISTS操作
     */
    private void handerExists(String value, boolean isNot) {
        if (StringUtils.isNotBlank(value)) {
            StringBuilder inSql = new StringBuilder();
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" EXISTS (").append(value).append(")");
            SqlSafe.securityCheck(new SqlConditionElement(inSql.toString()));// 安全检查 ,未通过检查则会抛出异常
            WHERE(inSql.toString());
        }
    }

    /**
     * 将NOT_EXISTS语句添加到WHERE条件中
     *
     * @param value
     * @return
     */
    public SqlMake NOT_EXISTS(String value) {
        handerExists(value, true);
        return this;
    }


}