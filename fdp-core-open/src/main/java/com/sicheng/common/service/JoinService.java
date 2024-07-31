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
package com.sicheng.common.service;

import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.persistence.DataEntity;
import com.sicheng.common.persistence.JoinDao;
import com.sicheng.common.persistence.Page;
import com.sicheng.common.persistence.wrapper.Wrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>标题: Join Service基类 </p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @param <D>
 * @param <T>
 * @author zhaolei
 * @date 2022年4月4日 下午10:47:04
 */
@Transactional(propagation = Propagation.SUPPORTS)
public abstract class JoinService<D extends JoinDao<T>, T extends DataEntity<T>> extends BaseService {
    /**
     * sqlSessionFactory
     */
    @Autowired
    SqlSessionFactoryBean sqlSessionFactory;

    /**
     * 缓存接口
     */
    @Autowired
    protected ShopCache cache;

    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;


    /**
     * Join连表查询
     * 获取单条数据
     *
     * @param id 主键
     * @return
     */
    public T joinSelectById(Long id) {
        return dao.joinSelectById(id);
    }

    /**
     * Join连表查询
     * 查询 select * from a where id in(…)
     *
     * @param list 主键集合
     * @return
     */
    public List<T> joinSelectByIdIn(List<?> list) {
        return dao.joinSelectByIdIn(list);
    }

    /**
     * Join连表查询分页数据
     * 查询数据列表，支持分页，分页对象可控制order by 排序
     *
     * @param page    分页对象
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public Page<T> joinSelectByWhere(Page<T> page, Wrapper wrapper) {
        List<T> list = dao.joinSelectByWhere(page, wrapper);
        if (page == null) {
            page = new Page<T>();
        }
        page.setList(list);
        return page;
    }

    /**
     * Join连表查询数据List
     * 查询数据列表，不支持分页
     *
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> joinSelectByWhere( Wrapper wrapper) {
        return dao.joinSelectByWhere(null, wrapper);
    }

    /**
     * Join连表查询
     * 查询所有数据列表
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> joinSelectAll(Wrapper wrapper) {
        List<T> list = dao.joinSelectAll(wrapper);
        return list;
    }

    /**
     * Join连表查询
     * 根据条件查询记录总数
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制del_flag、控制distinct
     * @return 总行数
     * <p>
     * 注意：data.setDelFlag(null);//把del_flag设为null，表示del_flag不做为条件查询，请你根据业务情况自己把握
     */
    public int joinCountByWhere(Wrapper wrapper) {
        return dao.joinCountByWhere(wrapper);
    }

}
