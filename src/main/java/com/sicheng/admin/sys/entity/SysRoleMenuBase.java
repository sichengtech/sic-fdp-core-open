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


import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.DataEntity;

/**
 * 系统角色、资源中间表 Entity 父类
 *
 * @author zjl
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