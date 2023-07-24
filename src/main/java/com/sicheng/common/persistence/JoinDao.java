/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence;

import com.sicheng.common.persistence.wrapper.Wrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>标题: Join DAO支持类实现</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @param <T>
 * @author zhaolei
 * @date 2022年4月4日 下午8:47:13
 */
public interface JoinDao<T> extends BaseDao {

    /**
     * Join连表查询
     * 获取单条数据
     *
     * @param id 主键
     * @return
     */
    public T joinSelectById(@Param(value = "id") Long id);

    /**
     * Join连表查询
     * 查询 select * from a where id in(…)
     *
     * @param list 主键集合
     * @return
     */
    public List<T> joinSelectByIdIn(@Param(value = "list") List<?> list);

    /**
     * Join连表查询
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     *                入参为null，或入参为new一个Wrapper对象但无属性值，会查全表
     * @param page    分页，null表示不分页
     * @return
     */
    public List<T> joinSelectByWhere(@Param(value = "p") Page<T> page, @Param(value = "w") Wrapper wrapper);

    /**
     * Join连表查询
     * 查询所有数据列表
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> joinSelectAll(@Param(value = "w") Wrapper wrapper);

    /**
     * Join连表查询
     * 根据条件查询记录总数
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制del_flag、控制distinct
     * @return 总行数
     * <p>
     * 注意：data.setDelFlag(null);//把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int joinCountByWhere(@Param(value = "w") Wrapper wrapper);

}