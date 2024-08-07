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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

import java.util.Date;

/**
 * 用户 Entity 父类
 *
 * @author fxx
 * @version 2017-02-14
 */
public class UserBase<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;
    private Office company;                 // 归属公司
    private Office office;                  // 归属部门
    private String loginName;               // 登录名
    private String password;                // 密码
    private String no;                      // 工号
    private String name;                    // 姓名
    private String email;                   // 邮箱
    private String phone;                   // 电话
    private String mobile;                  // 手机
    private String userType;                // 用户类型
    private String photo;                   // 头像
    private String loginIp;                 // 最后登录IP
    private Date loginDate;                 // 最后登录日期
    private String loginFlag;               // 是否允许登录
    private String qq;                      // qq
    @JsonIgnore
    private String bak1;                    // 备用字段1
    @JsonIgnore
    private String bak2;                    // 备用字段2
    @JsonIgnore
    private String bak3;                    // 备用字段3
    @JsonIgnore
    private String bak4;                    // 备用字段4
    @JsonIgnore
    private String bak5;                    // 备用字段5
    private Date beginLoginDate;            // 开始 最后登录日期
    private Date endLoginDate;              // 结束 最后登录日期
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date
    private Date beginUpdateDate;           // 开始 update_date
    private Date endUpdateDate;             // 结束 update_date

    public UserBase() {
        super();
    }

    public UserBase(Long id) {
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
     * getter company(归属公司)
     */
    public Office getCompany() {
        return company;
    }

    /**
     * setter company(归属公司)
     */
    public void setCompany(Office company) {
        this.company = company;
    }

    /**
     * getter office(归属部门)
     */
    public Office getOffice() {
        return office;
    }

    /**
     * setter office(归属部门)
     */
    public void setOffice(Office office) {
        this.office = office;
    }

    /**
     * getter loginName(登录名)
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * setter loginName(登录名)
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * getter password(密码)
     */
    public String getPassword() {
        return password;
    }

    /**
     * setter password(密码)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * getter no(工号)
     */
    public String getNo() {
        return no;
    }

    /**
     * setter no(工号)
     */
    public void setNo(String no) {
        this.no = no;
    }

    /**
     * getter name(姓名)
     */
    public String getName() {
        return name;
    }

    /**
     * setter name(姓名)
     */
    public void setName(String name) {
        this.name = name;
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
     * getter mobile(手机)
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * setter mobile(手机)
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * getter userType(用户类型)
     */
    public String getUserType() {
        return userType;
    }

    /**
     * setter userType(用户类型)
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * getter photo(头像)
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * setter photo(头像)
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * getter loginIp(最后登录IP)
     */
    public String getLoginIp() {
        return loginIp;
    }

    /**
     * setter loginIp(最后登录IP)
     */
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * getter loginDate(最后登录日期)
     */
    public Date getLoginDate() {
        return loginDate;
    }

    /**
     * setter loginDate(最后登录日期)
     */
    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    /**
     * getter loginFlag(是否允许登录)
     */
    public String getLoginFlag() {
        return loginFlag;
    }

    /**
     * setter loginFlag(是否允许登录)
     */
    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    /**
     * getter qq(qq)
     */
    public String getQq() {
        return qq;
    }

    /**
     * setter qq(qq)
     */
    public void setQq(String qq) {
        this.qq = qq;
    }

    /**
     * getter bak1(bak1)
     */
    public String getBak1() {
        return bak1;
    }

    /**
     * setter bak1(bak1)
     */
    public void setBak1(String bak1) {
        this.bak1 = bak1;
    }

    /**
     * getter bak2(bak2)
     */
    public String getBak2() {
        return bak2;
    }

    /**
     * setter bak2(bak2)
     */
    public void setBak2(String bak2) {
        this.bak2 = bak2;
    }

    /**
     * getter bak3(bak3)
     */
    public String getBak3() {
        return bak3;
    }

    /**
     * setter bak3(bak3)
     */
    public void setBak3(String bak3) {
        this.bak3 = bak3;
    }

    /**
     * getter bak4(bak4)
     */
    public String getBak4() {
        return bak4;
    }

    /**
     * setter bak4(bak4)
     */
    public void setBak4(String bak4) {
        this.bak4 = bak4;
    }

    /**
     * getter bak5(bak5)
     */
    public String getBak5() {
        return bak5;
    }

    /**
     * setter bak5(bak5)
     */
    public void setBak5(String bak5) {
        this.bak5 = bak5;
    }

    /**
     * getter loginDate(最后登录日期)
     */
    public Date getBeginLoginDate() {
        return beginLoginDate;
    }

    /**
     * setter loginDate(最后登录日期)
     */
    public void setBeginLoginDate(Date beginLoginDate) {
        this.beginLoginDate = beginLoginDate;
    }

    /**
     * getter loginDate(最后登录日期)
     */
    public Date getEndLoginDate() {
        return endLoginDate;
    }

    /**
     * setter loginDate(最后登录日期)
     */
    public void setEndLoginDate(Date endLoginDate) {
        this.endLoginDate = endLoginDate;
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