/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.dao;

import com.sicheng.admin.sys.entity.User;
import com.sicheng.common.persistence.CrudDao;
import com.sicheng.common.persistence.annotation.MyBatisDao;

/**
 * 用户DAO接口
 *
 * @author zhaolei
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {

    //请在这里增加你自己的DAO层方法

    //14条单表操作的通用SQL调用方法都在父类中，全继承下来了，可直接使用。

}
