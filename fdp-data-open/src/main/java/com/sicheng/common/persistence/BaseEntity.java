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
package com.sicheng.common.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity支持类
 *
 * @author zhaolei
 * @version 2014-05-16
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";


    /**
     * 实体编号（唯一标识）
     */
    protected Long id;

    /**
     * 自定义SQL（SQL标识，SQL内容）
     */
    @JsonIgnore
    protected Map<String, String> sqlMap;

    /**
     * 主键生成策略 （由开发人员选用）
     * 1：由业务指定主键，如user.setId(100)或由FDP框架生成主键，并保证唯一性。当getPkMode()==1 && getIsNewRecord()时生成
     * 2：由DB负责生成，并返回给java程序供后续使用。当getPkMode()==2时生成，（mysql使用自增长、oracle使用序列）
     */
    @JsonIgnore
    protected int pkMode = 2;


    /**
     * 数据库类型
     * 指明数据库类型，数据库类型值：mysql、oracle、mssql、postgresql
     * 用途：在底层 Mybatis的Mapper.xml文件中，通过dbType$来判断目标数据库是哪一种数据库技术
     */
    @JsonIgnore
    protected String dbType$ = "mysql";

    /**
     * 主键生成方式 （由开发人员选用）
     */
    @JsonIgnore
    public int getPkMode() {
        return pkMode;
    }

    /**
     * 主键生成方式 （由开发人员选用）
     */
    public void setPkMode(int pkMode) {
        this.pkMode = pkMode;
    }

    public BaseEntity() {

    }

    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @XmlTransient
    public Map<String, String> getSqlMap() {
        if (sqlMap == null) {
            sqlMap = Maps.newHashMap();
        }
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    /**
     * 是否是新记录(无ID就是新记录，需要自动生成ID)
     *
     * @return
     */
    @JsonIgnore
    public boolean getIsNewRecord() {
        return getId() == null;
    }

    /**
     * 获取数据库类型，数据库类型值：mysql、oracle、mssql、postgresql
     * @return 数据库类型的字符串
     */
    @JsonIgnore
    public String getDbType$() {
        return dbType$;
    }

    /**
     * 设置  数据库类型，数据库类型值：mysql、oracle、mssql、postgresql
     */
    public void setDbType$(String dbType$) {
        this.dbType$ = dbType$;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity that = (BaseEntity) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * Bean工具类--批量调用对象的getXxx()方法
     * 有一个List,比如List<Object> list，内有10个实体对象
     * 调用这10个对象的getXxx()方法，取出值放在List返回。
     * 为id in(list) 这种SQL为准备。
     *
     * @param list      一批实体对象
     * @param fieldName 属性名
     *                  id表示取id的值,
     *                  user.id表示先出user,再取user的id。支持无限级联。
     * @return
     */
    public static List<Object> batchField(List<?> list, String fieldName) {
        return null;
    }

    /**
     * 循环填充
     *
     * @param fromList          来源
     * @param fromFieldName     对比的属性名,id表示取id的值。user.id表示先出user,再取user的id。支持无限级联。
     * @param toList            目标
     * @param toFieldName       对比的属性名,id表示取id的值。user.id表示先出user,再取user的id。支持无限级联。
     * @param toEntityFieldName 调用目标的本方法设置值,（还不支持级联）
     */
    public static void fill(List<?> fromList, String fromFieldName, List<?> toList, String toFieldName, String toEntityFieldName) {

    }


}
