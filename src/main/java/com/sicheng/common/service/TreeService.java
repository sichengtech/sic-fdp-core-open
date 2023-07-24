/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.service;

import com.sicheng.common.persistence.TreeDao;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.common.persistence.TreeEntity;
import com.sicheng.common.utils.Reflections;
import com.sicheng.common.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结构的Service基类
 *
 * @author zhaolei
 * @version 2014-05-16
 */
@Transactional(propagation = Propagation.SUPPORTS)
public abstract class TreeService<D extends TreeDao<T>, T extends TreeEntity<T>> extends CrudService<D, T> {

    /**
     * <p>描述:
     * 1、保存节点，并正确维护父子关系。
     * 2、移动节点，并正确维护父子关系。
     * </p>
     * 修改当前节点的父节点，并同时找出当前节点的所有子节点，批量修改"父节点"
     *
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(T entity) {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = Reflections.getClassGenricType(getClass(), 1);//通过反射, 获得Class定义中声明的父类的泛型参数的类型.

        // 如果没有设置父节点，则代表为根节点，有则获取父节点实体
        if (entity.getParent() == null || entity.getParentId() == null || "0".equals(entity.getParentId())) {
            entity.setParent(null);
        } else {
            entity.setParent(super.selectById(entity.getParentId()));//根据ID查出一个实体
        }
        if (entity.getParent() == null) {
            T parentEntity = null;
            try {
                parentEntity = entityClass.getConstructor(Long.class).newInstance(0L);
            } catch (Exception e) {
                throw new ServiceException(e);
            }
            entity.setParent(parentEntity);//父节点的ID设为0
            entity.getParent().setParentIds(StringUtils.EMPTY);
        }

        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = entity.getParentIds();

        // 设置新的父节点串
        entity.setParentIds(entity.getParent().getParentIds() + entity.getParent().getId() + ",");

        // 保存或更新实体
        //super.save(entity);
        super.insertOrUpdate(entity);

        List<T> list = this.findChildNodeAll(entity.getId());//找到所有子节点
        for (T e : list) {
            if (e.getParentIds() != null && oldParentIds != null) {
                e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
                preUpdateChild(entity, e);
                //dao.updateParentIds(e);  //更新单条数据"所有父节点"字段
                //本只想更新parent_ids一个字段,目前更新了所有字段，未来优化
                this.updateByIdSelective(e);//更新单条数据"所有父节点"字段
            }
        }
    }

    /**
     * 预留接口，用户更新子节前调用
     *
     * @param childEntity
     */
    protected void preUpdateChild(T entity, T childEntity) {

    }

    /**
     * 查找特定节点的 兄弟节点，（不含自己）
     *
     * @param id 特定节点的ID
     * @return 兄弟节点List
     */
    public List<T> findSiblingNode(Long id) {
        T entity = super.selectById(id);//用id查出实体对象
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_id=", entity.getParentId());//父ID相同的就是兄弟节点
        wrapper.orderBy("a.sort ASC");
        List<T> list = super.selectByWhere(wrapper);
        List<T> rs_list = new ArrayList<T>();
        for (T tmp : list) {
            Long current_id = tmp.getId();
            if (current_id != id) {//过滤掉"自己"
                rs_list.add(tmp);
            }
        }
        return rs_list;
    }

    /**
     * 查找特定节点是否有兄弟节点
     *
     * @param id
     * @return 有true, 无false
     */
    public boolean hasSiblingNode(Long id) {
        T entity = super.selectById(id);//用id查出实体对象
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_id=", entity.getParentId());//父ID相同的就是兄弟节点
        int count = super.countByWhere(wrapper);
        if (count > 1) {//为什么要>1而不是>0?因为1是自己，可过滤掉自己
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找特定节点的 父节点 (不含自身)
     *
     * @param id 特定节点的ID
     * @return 父节点List，并且父节点是有“爷父子孙”序的，如：1,2,13,17
     */
    public List<T> findParentNode(Long id) {
        T entity = super.selectById(id);//用id查出实体对象
        if (entity == null) {
            return new ArrayList<T>();
        }
        String ids = entity.getParentIds();//ids的值结构是：0,1,2,13,17,
        String[] arr = ids.split(",");
        List<Long> id_list = new ArrayList<Long>();
        for (int i = 1; i < arr.length; i++) {
            //i=1，跳过第一个值为0的ID，它只是一个占位，并不代表一个节点
            String b = arr[i];
            id_list.add(Long.valueOf(b));
        }
        List<T> list = super.selectByIdIn(id_list);
        return list;
    }

    /**
     * 查找特定节点的 父节点 (不含自身)
     *
     * @param id 特定节点的ID
     * @return 父节点
     */
    public T findParentNodeOne(Long id) {
        T entity = super.selectById(id);//用id查出实体对象
        String ids = entity.getParentIds();//ids的值结构是：0,1,2,13,17,
        String[] arr = ids.split(",");
        T entityParent = super.selectById(Long.parseLong(arr[arr.length - 1]));
        return entityParent;
    }

    /**
     * 查找特定节点是否有父节点
     * 并不是简单的判断parentIds属性的,而是真的查询了库，所有得到的结果是真实可信的
     *
     * @param id 特定节点的ID
     * @return
     */
    public boolean hasdParentNode(Long id) {
        List<T> list = this.findParentNode(id);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找特定节点的 一级子节点（是第一层子节点，不含子子节点）
     *
     * @param id 特定节点的ID
     * @return 一级子节点List（第一层子节点）
     */
    public List<T> findChildNode(Long id) {
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_id=", id);//找第一层子节点
        wrapper.orderBy("a.sort ASC");
        List<T> list = super.selectByWhere(wrapper);
        return list;
    }

    /**
     * 查找特定节点是否有 一级子节点（是第一层子节点，不含子子节点）
     *
     * @param id 特定节点的ID
     * @return
     */
    public boolean hadChildNode(Long id) {
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_id=", id);//找第一层子节点
        int rs = super.countByWhere(wrapper);
        if (rs > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查找特定节点的 所有子节点（含子子节点、子子子节点、....）
     *
     * @param id 特定节点的ID
     * @return 子节点List
     */
    public List<T> findChildNodeAll(Long id) {
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_ids like", "%," + id + ",%");//找所有子节点（含子子节点、子子子节点、....）
        wrapper.orderBy("a.sort ASC");
        List<T> list = super.selectByWhere(wrapper);
        return list;
    }

    /**
     * 删除 特定节点的 所有子节点（含子子节点、子子子节点、....）
     *
     * @param id 特定节点的ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteChildNodeAll(Long id) {
        Wrapper wrapper = new Wrapper();
        wrapper.and("a.parent_ids like", "%," + id + ",%");//所有子节点（含子子节点、子子子节点、....）
        return super.deleteByWhere(wrapper);
    }

}
