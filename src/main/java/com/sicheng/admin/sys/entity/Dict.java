/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

/**
 * 字典 Entity 子类，请把你的业务代码写在这里
 *
 * @author 范秀秀
 * @version 2017-02-09
 */
public class Dict extends DictBase<Dict> {

    private static final long serialVersionUID = 1L;

    public Dict() {
        super();
    }

    public Dict(Long id) {
        super(id);
    }

    public Dict(String value, String label) {
        setValue(value);
        setLabel(label);
    }

    @Override
    public String toString() {
        return getLabel();
    }

}