/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

import java.util.Date;

/**
 * 系统菜单 Entity 父类
 *
 * @author 赵磊
 * @version 2017-02-13
 */
public class MenuBase<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;
    private Menu parent;                    // parent_id
    private String parentIds;               // parent_ids
    private String name;                    // name
    private Integer sort;                   // sort
    private String href;                    // href
    private String target;                  // target
    private String icon;                    // icon
    private String isShow;                  // is_show
    private String permission;              // permission
    private String menuNum;                 // menu_num
    private Date beginCreateDate;           // 开始 create_date
    private Date endCreateDate;             // 结束 create_date
    private Date beginUpdateDate;           // 开始 update_date
    private Date endUpdateDate;             // 结束 update_date

    public MenuBase() {
        super();
    }

    public MenuBase(Long id) {
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

    @JsonBackReference
    /**
     * getter parent(parent_id)
     */
    public Menu getParent() {
        return parent;
    }

    /**
     * setter parent(parent_id)
     */
    public void setParent(Menu parent) {
        this.parent = parent;
    }

    /**
     * getter parentIds(parent_ids)
     */
    public String getParentIds() {
        return parentIds;
    }

    /**
     * setter parentIds(parent_ids)
     */
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    /**
     * getter name(name)
     */
    public String getName() {
        return name;
    }

    /**
     * setter name(name)
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * getter href(href)
     */
    public String getHref() {
        return href;
    }

    /**
     * setter href(href)
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * getter target(target)
     */
    public String getTarget() {
        return target;
    }

    /**
     * setter target(target)
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * getter icon(icon)
     */
    public String getIcon() {
        return icon;
    }

    /**
     * setter icon(icon)
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * getter isShow(is_show)
     */
    public String getIsShow() {
        return isShow;
    }

    /**
     * setter isShow(is_show)
     */
    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    /**
     * getter permission(permission)
     */
    public String getPermission() {
        return permission;
    }

    /**
     * setter permission(permission)
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * getter menuNum(menu_num)
     */
    public String getMenuNum() {
        return menuNum;
    }

    /**
     * setter menuNum(menu_num)
     */
    public void setMenuNum(String menuNum) {
        this.menuNum = menuNum;
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