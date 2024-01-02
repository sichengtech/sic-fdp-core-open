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