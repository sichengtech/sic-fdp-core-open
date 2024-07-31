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