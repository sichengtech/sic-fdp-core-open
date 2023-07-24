/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.TreeEntity;

import java.util.Date;

/**
 * 地区 Entity 父类
 *
 * @author 范秀秀
 * @version 2017-02-13
 */
public class AreaBase<T> extends TreeEntity<T> {

    private static final long serialVersionUID = 1L;
    private String code;                    // code
    private String type;                    // type
    private String largeArea;               // 大区
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date
    private Date beginUpdateDate;           // 开始 update_date
    private Date endUpdateDate;             // 结束 update_date

    public AreaBase() {
        super();
    }

    public AreaBase(Long id) {
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
     * getter code(code)
     */
    public String getCode() {
        return code;
    }

    /**
     * setter code(code)
     */
    public void setCode(String code) {
        this.code = code;
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
     * getter largeArea(大区)
     */
    public String getLargeArea() {
        return largeArea;
    }

    /**
     * setter largeArea(大区)
     */
    public void setLargeArea(String largeArea) {
        this.largeArea = largeArea;
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