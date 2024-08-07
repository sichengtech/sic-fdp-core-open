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