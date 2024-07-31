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