/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sicheng.common.utils.Reflections;
import org.hibernate.validator.constraints.Length;

/**
 * 树结构的 Entity基类
 *
 * @author zhaolei
 * @version 2014-05-16
 */
public class TreeEntity<T> extends DataEntity<T> {
    private static final long serialVersionUID = 1L;

    protected String parentIds; // 所有父级ID，多个值用逗号隔开，如：0,1,2,13,17,
    protected String name;        // 名称
    protected Integer sort;        // 排序
    protected T parent;            // 父级实体(父级ID在这里)

    public TreeEntity() {
        super();
//		this.sort = 30;
    }

    public TreeEntity(Long id) {
        super(id);
    }

    @Length(min = 1, max = 2000)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * @return the parent
     */
    @JsonBackReference
    public T getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    @JsonBackReference
    public void setParent(T parent) {
        this.parent = parent;
    }

    /**
     * 获取父类的ID
     *
     * @return
     */
    public Long getParentId() {
        Long id = null;
        if (parent != null) {
            id = (Long) Reflections.getFieldValue(parent, "id");
        }
        return id != null ? id : 0L;
    }

}