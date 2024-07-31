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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Date;

/**
 * 字典 Entity 父类
 *
 * @author fxx
 * @version 2017-02-09
 */
public class DictBase<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;
    private String value;                   // value
    private String label;                   // label
    private String type;                    // type
    private String description;             // description
    private Integer sort;                   // sort
    private Dict parent;                    // parent_id
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date
    private Date beginUpdateDate;           // 开始 update_date
    private Date endUpdateDate;             // 结束 update_date

    public DictBase() {
        super();
    }

    public DictBase(Long id) {
        super(id);
    }

    /**
     * 描述: 获取ID
     *
     * @return
     * @see BaseEntity#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * 描述: 设置ID
     *
     * @param id
     * @see BaseEntity#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * getter value(value)
     */
    @XmlAttribute
    public String getValue() {
        return value;
    }

    /**
     * setter value(value)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * getter label(label)
     */
    @XmlAttribute
    public String getLabel() {
        return label;
    }

    /**
     * setter label(label)
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * getter type(type)
     */
    public String getType() {
        return type;
    }

    /**
     * setter type(type)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getter description(description)
     */
    @XmlAttribute
    public String getDescription() {
        return description;
    }

    /**
     * setter description(description)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getter sort(sort)
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * setter sort(sort)
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @JsonBackReference
    /**
     * getter parent(parent_id)
     */
    public Dict getParent() {
        return parent;
    }

    /**
     * setter parent(parent_id)
     */
    public void setParent(Dict parent) {
        this.parent = parent;
    }

    /**
     * getter createDate(create_date)
     */
    public Date getBeginCreateDate() {
        return beginCreateDate;
    }

    /**
     * setter createDate(create_date)
     */
    public void setBeginCreateDate(Date beginCreateDate) {
        this.beginCreateDate = beginCreateDate;
    }

    /**
     * getter createDate(create_date)
     */
    public Date getEndCreateDate() {
        return endCreateDate;
    }

    /**
     * setter createDate(create_date)
     */
    public void setEndCreateDate(Date endCreateDate) {
        this.endCreateDate = endCreateDate;
    }

    /**
     * getter updateDate(update_date)
     */
    public Date getBeginUpdateDate() {
        return beginUpdateDate;
    }

    /**
     * setter updateDate(update_date)
     */
    public void setBeginUpdateDate(Date beginUpdateDate) {
        this.beginUpdateDate = beginUpdateDate;
    }

    /**
     * getter updateDate(update_date)
     */
    public Date getEndUpdateDate() {
        return endUpdateDate;
    }

    /**
     * setter updateDate(update_date)
     */
    public void setEndUpdateDate(Date endUpdateDate) {
        this.endUpdateDate = endUpdateDate;
    }

}