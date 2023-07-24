/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import java.util.List;

/**
 * 部门 Entity 子类，请把你的业务代码写在这里
 *
 * @author 范秀秀
 * @version 2017-02-14
 */
public class Office extends OfficeBase<Office> {

    private static final long serialVersionUID = 1L;

    public Office() {
        super();
        setSort(30);
    }

    public Office(Long id) {
        super(id);
    }

    private List<String> childDeptList;//快速添加子部门

    public List<String> getChildDeptList() {
        return childDeptList;
    }

    public void setChildDeptList(List<String> childDeptList) {
        this.childDeptList = childDeptList;
    }

}