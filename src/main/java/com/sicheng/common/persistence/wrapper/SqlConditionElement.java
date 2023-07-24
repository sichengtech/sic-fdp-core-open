/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.wrapper;

/**
 * <p>标题: SqlConditionElement</p>
 * <p>描述: 一个SqlConditionElement表示sql语句中的一个where条件，SqlConditionGroup条件组中有多个SqlConditionElement</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年1月30日 下午6:48:39
 */
public class SqlConditionElement {

    private String condition;
    private Object[] values;

    public String getCondition() {
        return condition;
    }

    public Object[] getValues() {
        return values;
    }

    public SqlConditionElement(String condition) {
        super();
        this.condition = condition;
    }

    public SqlConditionElement(String condition, Object... params) {
        super();
        this.condition = condition;
        this.values = params;
    }

}