/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.TreeEntity;

import java.util.Date;

/**
 * 部门 Entity 父类
 *
 * @author 范秀秀
 * @version 2017-02-14
 */
public class OfficeBase<T> extends TreeEntity<T> {

    private static final long serialVersionUID = 1L;
    private Area area;                      // 归属区域
    private String code;                    // 机构编码
    private String type;                    // 机构类型（1：公司；2：部门；3：小组）
    private String grade;                   // 机构等级（1：一级；2：二级；3：三级；4：四级）
    private String address;                 // 联系地址
    private String zipCode;                 // 邮政编码
    private String master;                  // 负责人
    private String phone;                   // 电话
    private String fax;                     // 传真
    private String email;                   // 邮箱
    private String useable;                 // 是否可用
    private User primaryPerson;             // 主负责人
    private User deputyPerson;              // 副负责人
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date
    private Date beginUpdateDate;           // 开始 update_date
    private Date endUpdateDate;             // 结束 update_date

    public OfficeBase() {
        super();
    }

    public OfficeBase(Long id) {
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
     * getter area(归属区域)
     */
    public Area getArea() {
        return area;
    }

    /**
     * setter area(归属区域)
     */
    public void setArea(Area area) {
        this.area = area;
    }

    /**
     * getter code(机构编码)
     */
    public String getCode() {
        return code;
    }

    /**
     * setter code(机构编码)
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * getter type(机构类型（1：公司；2：部门；3：小组）)
     */
    public String getType() {
        return type;
    }

    /**
     * setter type(机构类型（1：公司；2：部门；3：小组）)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getter grade(机构等级（1：一级；2：二级；3：三级；4：四级）)
     */
    public String getGrade() {
        return grade;
    }

    /**
     * setter grade(机构等级（1：一级；2：二级；3：三级；4：四级）)
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * getter address(联系地址)
     */
    public String getAddress() {
        return address;
    }

    /**
     * setter address(联系地址)
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * getter zipCode(邮政编码)
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * setter zipCode(邮政编码)
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * getter master(负责人)
     */
    public String getMaster() {
        return master;
    }

    /**
     * setter master(负责人)
     */
    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * getter phone(电话)
     */
    public String getPhone() {
        return phone;
    }

    /**
     * setter phone(电话)
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * getter fax(传真)
     */
    public String getFax() {
        return fax;
    }

    /**
     * setter fax(传真)
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * getter email(邮箱)
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter email(邮箱)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getter useable(是否可用)
     */
    public String getUseable() {
        return useable;
    }

    /**
     * setter useable(是否可用)
     */
    public void setUseable(String useable) {
        this.useable = useable;
    }

    /**
     * getter primaryPerson(主负责人)
     */
    public User getPrimaryPerson() {
        return primaryPerson;
    }

    /**
     * setter primaryPerson(主负责人)
     */
    public void setPrimaryPerson(User primaryPerson) {
        this.primaryPerson = primaryPerson;
    }

    /**
     * getter deputyPerson(副负责人)
     */
    public User getDeputyPerson() {
        return deputyPerson;
    }

    /**
     * setter deputyPerson(副负责人)
     */
    public void setDeputyPerson(User deputyPerson) {
        this.deputyPerson = deputyPerson;
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