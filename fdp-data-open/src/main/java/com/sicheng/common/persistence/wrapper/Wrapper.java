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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Map;

/**
 * <p>标题: Wrapper</p>
 * <p>描述: SQL语句的where条件组装工具类。可灵活的组装where条件</p>
 * <p>
 * 支持写括号来确定多个条件的运算优先级：where (name='abc' and age=18) or (addr='北京')
 * 支持的条件关键字：where、and、or、group by、having、order by desc asc、distinct
 * 支持的运算符：=、<>、is null、is not null、>、>=、<、<=、like %?%、like _?_、in、not in、between、not between、exists、not exists
 * 注意：强制参数要使用占位符来传递，Wrapper的占位符{0}{1}，分为显式占位符、隐式占位符两种。
 * 目标是防止SQL注入，不可直接把参数拼到sql内,如where name='张三'，由于'张三'是互联网用户通过表单输入进入的会有SQL注入攻击的风险。
 * 注意：groupBy\orderBy 子句的防止SQL注入工作由正则过滤器完成，你放心使用。
 * 注意：由于安全原因不支持写子查询,select被定义为危险字符
 * 注意：由于安全原因不支持在sql中写函数,'单引号被定义为危险字符,如to_date('','')函数，但有其它替换办法，请看以下的示例
 *
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @param
 * @author zhaolei
 * @date 2017年1月30日 上午11:37:19
 */
public class Wrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 在查询时，是否使用distinct
     */
    protected boolean distinct = false;

    /**
     * @Fields entity : 实体类
     */
    protected Object entity = null;

    /**
     * @Title:构造方法
     */
    public Wrapper() {
        super();
    }

    /**
     * @param entity
     * @Title:构造方法
     */
    public Wrapper(Object entity) {
        super();
        this.entity = entity;
    }

    /**
     * 输出SQL
     * 本SQL包含where条件部分，含order by部分
     */
    public String getOutputSqlAll() {
        return null;
    }

    /**
     * 输出SQL
     * 本SQL只包含where条件部分,不含order by部分
     */
    public String getOutputSqlWhere() {
        return null;
    }

    /**
     * 输出SQL
     * 本SQL只包含order by部分
     */
    public String getOutputSqlOrderBy() {
        return null;
    }

    /**
     * 输出参数占位符值（where条件的值）
     *
     * @return
     */
    public Map<String, Object> getValueMap() {
        return null;
    }

    /**
     * 输出参数占位符值（where条件的值）
     * 是getValueMap()的简写
     *
     * @return
     */
    public Map<String, Object> getV() {
        return getValueMap();
    }

    /**
     * <p>AND 连接后续条件</p>
     *
     * @param condition and条件语句
     * @param params 参数集
     * @return this
     */
    public Wrapper and(String condition, Object... params) {
        return null;
    }

    /**
     * 添加一个AND
     *
     * @return
     */
    public Wrapper and() {
        return null;
    }

    /**
     * <p>使用AND连接并开启一组新的(xxx)</p>
     * <p>
     * eg: ew.and("name=","zhangsan").and("id=",11).andNew("statu=",1);
     * 输出：AND (name='zhangsan' AND id=11) AND (statu=1)
     * </p>
     *
     * @param condition AND 条件语句
     * @param params 参数值
     * @return this
     */
    public Wrapper andNew(String condition, Object... params) {
        return null;
    }

    /**
     * <p>添加OR条件</p>
     *
     * @param sqlOr  or 条件语句
     * @param params 参数集
     * @return this
     */
    public Wrapper or(String sqlOr, Object... params) {
        return null;
    }

    /**
     * 添加一个 OR
     *
     * @return
     */
    public Wrapper or() {
        return null;
    }

    /**
     * <p>使用OR，并开启一组新的(xxx)</p>
     * <p>
     * eg: ew.where("name=",'zhangsan').and("id=",11).orNew("statu=",1);
     * 输出：AND (name='zhangsan' AND id=11) OR (statu=1)
     * </p>
     *
     * @param sqlOr  AND 条件语句
     * @param params 参数值
     * @return this
     */
    public Wrapper orNew(String sqlOr, Object... params) {
        return null;
    }

    /**
     * <p>SQL中groupBy关键字跟的条件语句</p>
     * <p>eg: ew.where("name=",'zhangsan').groupBy("id,name")</p>
     *
     * @param columns SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    public Wrapper groupBy(String columns) {
        return null;
    }

    /**
     * <p>SQL中having关键字跟的条件语句</p>
     * <p>eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")</p>
     *
     * @param sqlHaving having关键字后面跟随的语句
     * @param params    参数集
     * @return EntityWrapper
     */
    public Wrapper having(String sqlHaving, Object... params) {
        return null;
    }

    /**
     * <p>SQL中orderby关键字跟的条件语句</p>
     * <p>
     * eg: ew.groupBy("id,name ACS").having("id={0}",22).and("password is not null").orderBy("id,name")
     * </p>
     *
     * @param columns SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    public Wrapper orderBy(String columns) {
        return null;
    }

    /**
     * 处理查询接口传来的排序参数，转换格式。
     * 1、拼接orber by参数
     * 2、把传来的参数"userId"小驼峰格式转成"user_id"这种下线格式,例 "userId ,a.nameSize asc,userType desc" 转换为 "user_id ,a.name_size asc,user_type desc"
     * <p>
     * 如果sortStr不为空，则优化使用综合排序条件。
     * 如果sortStr为空，但sort不为空，则使用sort和sortMode排序条件。
     * <p>
     * 安全：Wrapper类内部有防止SQL注入的安全检查。包括orber by子句也被检查了，请放心使用。
     *
     * @param sortStr  综合排序条件,sortStr单独使用,优先级高于sort和sortMode,示例:"id desc,name asc,type desc"
     * @param sort     单个排序字段,sort和sortMode要配套使用(因只支持一个字段淘汰但可用)，取值范围：全部字段
     * @param sortMode 单个排序模式,sort和sortMode要配套使用(因只支持一个字段淘汰但可用)，取值范围：ASC,DESC
     */
    public void orderBy(String sortStr, String sort, String sortMode) {

    }

    /**
     * EXISTS 条件语句，目前适配mysql及oracle
     *
     * @param value 匹配值
     * @return this
     */
    public Wrapper exists(String value) {
        return null;
    }

    /**
     * NOT EXISTS条件语句
     *
     * @param value 匹配值
     * @return this
     */
    public Wrapper notExists(String value) {
        return null;
    }

    /**
     * @return the entity
     */
    public Object getEntity() {
        return entity;
    }

    /**
     * getE()是getEntity() 方法的简写
     *
     * @return
     */
    public Object getE() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Object entity) {
        this.entity = entity;
    }

    /**
     * @return the distinct
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @param distinct the distinct to set
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * 判断当前使用的数据库是否是mysql
     */
    @JsonIgnore
    public boolean isMysql() {

        return false;
    }

    /**
     * 获取数据库类型名称
     */
    @JsonIgnore
    public String getDbType$() {
        return null;
    }

}