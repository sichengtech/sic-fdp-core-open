/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;


import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

/**
 * 系统角色、资源中间表 Entity 父类
 *
 * @author 张加利
 * @version 2017-10-25
 */
public class SysRoleMenuBase<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;
    private Long roleId;                    // 角色编号
    private Long menuId;                    // 菜单编号

    public SysRoleMenuBase() {
        super();
    }

    public SysRoleMenuBase(Long id) {
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
     * getter roleId(角色编号)
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * setter roleId(角色编号)
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * getter menuId(菜单编号)
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * setter menuId(菜单编号)
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }


}