/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
package com.sicheng.common.persistence;

import com.sicheng.common.persistence.wrapper.Wrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>标题: DAO支持类实现</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @param <T>
 * @author zhaolei
 * @date 2016年5月15日 下午8:47:13
 */
public interface CrudDao<T> extends BaseDao {

    /**
     * 获取单条数据
     *
     * @param id 主键
     * @return
     */
    public T selectById(@Param(value = "id") Long id);

    /**
     * 查询 select * from a where id in(…)
     *
     * @param list 主键集合
     * @return
     */
    public List<T> selectByIdIn(@Param(value = "list") List<?> list);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     *                入参为null，或入参为new一个Wrapper对象但无属性值，会查全表
     * @param page    分页，null表示不分页
     * @return
     */
    public List<T> selectByWhere(@Param(value = "p") Page<T> page, @Param(value = "w") Wrapper wrapper);

    /**
     * 查询所有数据列表
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> selectAll(@Param(value = "w") Wrapper wrapper);

    /**
     * 插入数据
     * 如果要在entity中带回自增长生成的主键值，mybatis的xml中要添加<insert id="insertSelective" keyProperty="pageId" useGeneratedKeys="true">
     *
     * @param entity
     * @return 受影响的行数
     */
    public int insert(@Param(value = "entity") T entity);

    /**
     * 插入,只把非空的值插入到对应的字段
     * 如果要在entity中带回自增长生成的主键值，mybatis的xml中要添加<insert id="insertSelective" keyProperty="pageId" useGeneratedKeys="true">
     *
     * @param entity
     * @return 受影响的行数
     */
    public int insertSelective(@Param(value = "entity") T entity);

    /**
     * 根据主键更新记录,更新除了主键的所有字段
     *
     * @param entity
     * @return 受影响的行数
     */
    public int updateById(@Param(value = "entity") T entity);

    /**
     * 根据条件更新记录,更新除了主键的所有字段
     *
     * @param entity  数据实体，用于存储数据，这些数据将被update到表中
     * @param wrapper 条件，用于where条件，找出符合条件的数据。入参为null，或入参为new一个Wrapper对象但无属性值，执行sql时会报错，防止更新全表
     * @return 受影响的行数
     * <p>
     * 注意：wrapper.entity.setDelFlag(null);把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int updateByWhere(@Param(value = "entity") T entity, @Param(value = "w") Wrapper wrapper);

    /**
     * 根据主键更新记录,只把非空的值更到对应的字段
     *
     * @param entity 数据实体，用于存储数据，这些数据将被update到表中
     * @return 受影响的行数
     */
    public int updateByIdSelective(@Param(value = "entity") T entity);

    /**
     * 根据条件更新记录,只把非空的值更到对应的字段
     *
     * @param entity  数据实体，用于存储数据，这些数据将被update到表中
     * @param wrapper 条件，用于where条件，找出符合条件的数据。入参为null，或入参为new一个Wrapper对象但无属性值，执行sql时会报错，防止更新全表
     * @return 受影响的行数
     * <p>
     * 注意：wrapper.entity.setDelFlag(null);把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int updateByWhereSelective(@Param(value = "entity") T entity, @Param(value = "w") Wrapper wrapper);

    /**
     * 删除数据
     * （如果有del_flag字段，就逻辑删除，更新del_flag字段为1表示删除）
     * （如果无del_flag字段，就物理删除）
     *
     * @param id 主键
     * @return 受影响的行数
     */
    public int deleteById(@Param(value = "id") Long id);

    /**
     * 根据主键批量删除记录
     *
     * @param list 一批主键
     * @return
     */
    public int deleteByIdIn(@Param(value = "list") List<Object> list);

    /**
     * 删除数据（物理删除）
     *
     * @param wrapper 删除条件。入参为null，或入参为new一个Wrapper对象但无属性值，执行SQL会报错，防止删除全表
     * @return 受影响的行数
     * <p>
     * 注意：data.setDelFlag(null);//把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int deleteByWhere(@Param(value = "w") Wrapper wrapper);

    /**
     * 根据条件查询记录总数
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制del_flag、控制distinct
     * @return 总行数
     * <p>
     * 注意：data.setDelFlag(null);//把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int countByWhere(@Param(value = "w") Wrapper wrapper);

}