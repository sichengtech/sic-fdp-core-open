/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.sicheng.admin.sys.dao.RoleDao;
import com.sicheng.admin.sys.dao.SysUserRoleDao;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.common.utils.Collections3;
import com.sicheng.common.web.SpringContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户 Entity 子类，请把你的业务代码写在这里
 *
 * @author 范秀秀
 * @version 2017-02-08
 */
public class User extends UserBase<User> {

    private static final long serialVersionUID = 1L;
    private Role role;    // 根据角色查询用户条件
    private String oldLoginName;// 原登录名
    private String newPassword;    // 新密码
    private String oldLoginIp;    // 上次登录IP
    private Date oldLoginDate;    // 上次登录日期
    private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表
    private List<Long> roleIdList = Lists.newArrayList(); // 拥有角色列表

    public User() {
        super();
    }

    public User(Long id) {
        super(id);
    }

    public User(Long id, String loginName) {
        super(id);
        setLoginName(loginName);
    }

    public User(Role role) {
        super();
        this.role = role;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    //系统拆分时做的修改 zl 2017-3-16
    //@ExcelField(title="拥有角色", align=1, sort=800, fieldType=RoleListType.class)
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @JsonIgnore
    public List<Long> getRoleIdList() {
        SysUserRoleDao sysUserRoleDao = SpringContextHolder.getBean(SysUserRoleDao.class);
        List<SysUserRole> sysUserRoles = sysUserRoleDao.selectByWhere(null, new Wrapper(new SysUserRole()).and("user_id=", this.getId()));
        if (!sysUserRoles.isEmpty()) {
            List<Long> roleIds = new ArrayList<Long>();
            for (int i = 0; i < sysUserRoles.size(); i++) {
                roleIds.add(sysUserRoles.get(i).getRoleId());
            }
            RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
            roleList = roleDao.selectByWhere(null, new Wrapper().and("id in", roleIds));
        }
        for (Role role : roleList) {
            roleIdList.add(role.getId());
        }
        return roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    public String getRoleNames() {
        return Collections3.extractToString(roleList, "name", ",");
    }

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long id) {
        return id == 1L;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public String getOldLoginName() {
        return oldLoginName;
    }

    public void setOldLoginName(String oldLoginName) {
        this.oldLoginName = oldLoginName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldLoginIp() {
        return oldLoginIp;
    }

    public void setOldLoginIp(String oldLoginIp) {
        this.oldLoginIp = oldLoginIp;
    }

    public Date getOldLoginDate() {
        return oldLoginDate;
    }

    public void setOldLoginDate(Date oldLoginDate) {
        this.oldLoginDate = oldLoginDate;
    }

}