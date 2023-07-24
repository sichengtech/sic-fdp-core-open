/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 系统菜单 Entity 子类，请把你的业务代码写在这里
 *
 * @author 赵磊
 * @version 2017-02-13
 */
public class Menu extends MenuBase<Menu> {

    private static final long serialVersionUID = 1L;

    public Menu() {
        super();
        //setSort(30);  //实体类的属性不允许有默认值  2020-10-31 zl
        //setIsShow("1"); //实体类的属性不允许有默认值  2020-10-31 zl
    }

    public Menu(Long id) {
        super(id);
    }

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 对菜单树按父子层级进行排序
     *
     * @param targetList 输出List
     * @param sourceList 输入List
     * @param parentId   根节点的ID
     * @param cascade    是否查找下一级
     */
    @JsonIgnore
    public static void sortList2Tree(List<Menu> targetList, List<Menu> sourceList, Long parentId, boolean cascade) {
        for (int i = 0; i < sourceList.size(); i++) {
            Menu e = sourceList.get(i);
            if (e.getParent() != null && e.getParent().getId() != null && e.getParent().getId().equals(parentId)) {
                targetList.add(e);
                if (cascade) {
                    sortList2Tree(targetList, sourceList, e.getId(), cascade);
                }
            }
        }
    }

    /**
     * 顶级菜单的ID，这是一个固定值
     *
     * @return
     */
    @JsonIgnore
    public static Long getRootId() {
        return 1L;
    }

    /**
     * 是否是顶级菜单
     *
     * @param id
     * @return
     */
    @JsonIgnore
    public static boolean isRoot(Long id) {
        return 1L == id;
    }

    /**
     * 获取父ID
     *
     * @return
     */
    public Long getParentId() {
        return getParent() != null && getParent().getId() != null ? getParent().getId() : 0L;
    }

}