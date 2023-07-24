/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.dao;

import com.sicheng.admin.sys.entity.SysRoleMenu;
import com.sicheng.common.persistence.CrudDao;
import com.sicheng.common.persistence.annotation.MyBatisDao;

/**
 * 角色菜单中间表DAO接口
 *
 * @author 蔡龙
 * @version 2017-10-24
 */
@MyBatisDao
public interface SysRoleMenuDao extends CrudDao<SysRoleMenu> {

    //请在这里增加你自己的DAO层方法

    //14条单表操作的通用SQL调用方法都在父类中，全继承下来了，可直接使用。

}