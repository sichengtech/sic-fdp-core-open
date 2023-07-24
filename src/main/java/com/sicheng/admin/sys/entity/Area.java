/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.admin.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sicheng.admin.sys.dao.AreaDao;
import com.sicheng.common.web.SpringContextHolder;

/**
 * 地区 Entity 子类，请把你的业务代码写在这里
 *
 * @author 范秀秀
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
        if (!"0,".equals(this.parentIds)) {
            AreaDao dao = SpringContextHolder.getBean(AreaDao.class);
            String[] areaIds = this.getParentIds().split(",");
            StringBuffer areaNames = new StringBuffer();
            for (int i = 0; i < areaIds.length; i++) {
                if (!"0".equals(areaIds[i])) {
                    Area area = dao.selectById(Long.parseLong(areaIds[i]));
                    areaNames.append("-");
                    areaNames.append(area.getName());
                }
            }
            areaParentNames = areaNames.substring(1);
        }
        return areaParentNames;
    }

    public void setAreaParentNames(String areaParentNames) {
        this.areaParentNames = areaParentNames;
    }

    @JsonIgnore
    private Long level; //等级

    public Long getLevel() {
        String[] carIds = this.getParentIds().split(",");
        if (carIds.length == 1) {
            level = 1L;
        }
        if (carIds.length == 2) {
            level = 2L;
        }
        if (carIds.length == 3) {
            level = 3L;
        }
        if (carIds.length == 4) {
            level = 4L;
        }
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

}