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

import com.google.common.collect.Lists;
import com.sicheng.admin.sys.dao.MenuDao;
import com.sicheng.admin.sys.dao.SysRoleMenuDao;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.admin.sys.dao.OfficeDao;
import com.sicheng.admin.sys.dao.SysRoleOfficeDao;
import com.sicheng.common.config.Global;
import com.sicheng.common.web.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色 Entity 子类，请把你的业务代码写在这里
 *
 * @author 范秀秀
 * @version 2017-02-08
 */
public class Role extends RoleBase<Role> {

    private static final long serialVersionUID = 1L;

    public Role() {
        super();
        this.setDataScope(DATA_SCOPE_ALL);
        this.setUseable(Global.YES);
    }

    public Role(Long id) {
        super(id);
    }

    public Role(User user) {
        this();
        this.user = user;
    }

    private String oldName;    // 原角色名称
    private String oldEnname;    // 原英文名称
    private User user;        // 根据用户ID查询角色列表
    private List<Menu> menuList = Lists.newArrayList(); // 拥有菜单列表
    private List<Office> officeList = Lists.newArrayList(); // 按明细设置数据范围

    public static final String DATA_SCOPE_ALL = "1";//所有数据
    public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";//所在公司及以下数据
    public static final String DATA_SCOPE_COMPANY = "3";//所在公司数据
    public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";//所在部门及以下数据
    public static final String DATA_SCOPE_OFFICE = "5";//所在部门数据
    public static final String DATA_SCOPE_SELF = "8";//仅本人数据
    public static final String DATA_SCOPE_CUSTOM = "9";//按明细设置

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getOldEnname() {
        return oldEnname;
    }

    public void setOldEnname(String oldEnname) {
        this.oldEnname = oldEnname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public List<Long> getMenuIdList() {
        List<Long> menuIdList = Lists.newArrayList();
        SysRoleMenuDao sysRoleMenuDao = SpringContextHolder.getBean(SysRoleMenuDao.class);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuDao.selectByWhere(null, new Wrapper().and("role_id=", this.getId()));
        if (!sysRoleMenus.isEmpty()) {
            List<Long> menIds = new ArrayList<Long>();
            for (int i = 0; i < sysRoleMenus.size(); i++) {
                menIds.add(sysRoleMenus.get(i).getMenuId());
            }
            MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
            menuList = menuDao.selectByWhere(null, new Wrapper().and("id in", menIds));
        }
        for (Menu menu : menuList) {
            menuIdList.add(menu.getId());
        }
        return menuIdList;
    }

    public void setMenuIdList(List<Long> menuIdList) {
        menuList = Lists.newArrayList();
        for (Long menuId : menuIdList) {
            Menu menu = new Menu();
            menu.setId(menuId);
            menuList.add(menu);
        }
    }

    public String getMenuIds() {
        return StringUtils.join(getMenuIdList(), ",");
    }

    public void setMenuIds(String menuIds) {
        menuList = Lists.newArrayList();
        if (menuIds != null) {
            String[] ids = StringUtils.split(menuIds, ",");

            List<Long> menuIdList = new ArrayList<Long>(ids.length);
            for (String s : ids) {
                menuIdList.add(Long.valueOf(s));
            }
            setMenuIdList(menuIdList);
        }
    }

    public List<Office> getOfficeList() {
        SysRoleOfficeDao sysRoleOfficeDao = SpringContextHolder.getBean(SysRoleOfficeDao.class);
        List<SysRoleOffice> sysRoleOffices = sysRoleOfficeDao.selectByWhere(null, new Wrapper().and("role_id=", this.getId()));
        if (!sysRoleOffices.isEmpty()) {
            List<Long> officeIds = new ArrayList<Long>();
            for (int i = 0; i < sysRoleOffices.size(); i++) {
                officeIds.add(sysRoleOffices.get(i).getOfficeId());
            }
            OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
            officeList = officeDao.selectByWhere(null, new Wrapper().and("id in", officeIds));
        }
        return officeList;
    }

    public void setOfficeList(List<Office> officeList) {
        this.officeList = officeList;
    }

    public List<Long> getOfficeIdList() {
        List<Long> officeIdList = Lists.newArrayList();
        for (Office office : getOfficeList()) {
            officeIdList.add(office.getId());
        }
        return officeIdList;
    }

    public void setOfficeIdList(List<Long> officeIdList) {
        officeList = Lists.newArrayList();
        for (Long officeId : officeIdList) {
            Office office = new Office();
            office.setId(officeId);
            officeList.add(office);
        }
    }

    public String getOfficeIds() {
        return StringUtils.join(getOfficeIdList(), ",");
    }

    public void setOfficeIds(String officeIds) {
        officeList = Lists.newArrayList();
        if (officeIds != null) {
            String[] ids = StringUtils.split(officeIds, ",");

            List<Long> list = new ArrayList<Long>(ids.length);
            for (String s : ids) {
                list.add(Long.valueOf(s));
            }

            setOfficeIdList(list);
        }
    }

    /**
     * 获取权限字符串列表
     */
    public List<String> getPermissions() {
        List<String> permissions = Lists.newArrayList();
        for (Menu menu : menuList) {
            if (menu.getPermission() != null && !"".equals(menu.getPermission())) {
                permissions.add(menu.getPermission());
            }
        }
        return permissions;
    }
}