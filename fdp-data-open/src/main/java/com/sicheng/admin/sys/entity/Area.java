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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 地区 Entity 子类，请把你的业务代码写在这里
 *
 * @author fxx
 * @version 2017-02-13
 */
public class Area extends AreaBase<Area> {

    private static final long serialVersionUID = 1L;

    public Area() {
        super();
        setSort(30);
    }

    public Area(Long id) {
        super(id);
    }

    //对于实体类的扩展代码，请写在这里
    @JsonIgnore
    private String areaParentNames;  //父级名字

    public String getAreaParentNames() {
        return null;
    }

    public void setAreaParentNames(String areaParentNames) {
        this.areaParentNames = areaParentNames;
    }

    @JsonIgnore
    private Long level; //等级

    public Long getLevel() {
        return null;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

}