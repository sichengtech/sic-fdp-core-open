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
package com.sicheng.admin.sys.entity;

/**
 * 字典 Entity 子类，请把你的业务代码写在这里
 *
 * @author fxx
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