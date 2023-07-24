/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

import java.util.Date;

/**
 * 日志 Entity 父类
 *
 * @author 范秀秀
 * @version 2017-02-08
 */
public class SysLogBase<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;
    private String type;                    // 日志类型（1：接入日志；2：错误日志）
    private String title;                   // 日志标题
    private String remoteAddr;              // 操作用户的IP地址
    private String userAgent;               // 操作用户代理信息
    private String requestUri;              // 操作的URI
    private String method;                  // 操作的方式
    private String params;                  // 操作提交的数据
    private String exception;               // exception
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date

    public SysLogBase() {
        super();
    }

    public SysLogBase(Long id) {
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
     * getter type(日志类型（1：接入日志；2：错误日志）)
     */
    public String getType() {
        return type;
    }

    /**
     * setter type(日志类型（1：接入日志；2：错误日志）)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getter title(日志标题)
     */
    public String getTitle() {
        return title;
    }

    /**
     * setter title(日志标题)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getter remoteAddr(操作用户的IP地址)
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * setter remoteAddr(操作用户的IP地址)
     */
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     * getter userAgent(操作用户代理信息)
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * setter userAgent(操作用户代理信息)
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * getter requestUri(操作的URI)
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * setter requestUri(操作的URI)
     */
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * getter method(操作的方式)
     */
    public String getMethod() {
        return method;
    }

    /**
     * setter method(操作的方式)
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * getter params(操作提交的数据)
     */
    public String getParams() {
        return params;
    }

    /**
     * setter params(操作提交的数据)
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * getter exception(exception)
     */
    public String getException() {
        return exception;
    }

    /**
     * setter exception(exception)
     */
    public void setException(String exception) {
        this.exception = exception;
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

}