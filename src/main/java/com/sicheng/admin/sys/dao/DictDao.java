/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.dao;

import com.sicheng.admin.sys.entity.Dict;
import com.sicheng.common.persistence.CrudDao;
import com.sicheng.common.persistence.annotation.MyBatisDao;

import java.util.List;
import java.util.Map;

/**
 * 字典DAO接口
 *
 * @author zhaolei
 * @version 2014-05-16
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {
    //请在这里增加你自己的DAO层方法
    //14条单表操作的通用SQL调用方法都在父类中，全继承下来了，可直接使用。

    //查出字典的所有type类型，（按type分组去重）
    List<Map<String, String>> findTypeList(Dict dict);

    //按type(类型)、description（描述）查出该类型的字典项，并按sort排序降序
    List<Dict> findList(Dict entity);

    //查询同类型的键值相同的字典数据 的数量
    Integer selectSameNumber(Dict dict);

}
