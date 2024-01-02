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
package com.sicheng.common.web;

import com.sicheng.admin.sys.dao.MenuDao;
import com.sicheng.admin.sys.entity.Menu;
import com.sicheng.common.beanvalidator.BeanValidators;
import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.mapper.JsonMapper;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.common.utils.DateUtils;
import com.sicheng.common.utils.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 控制器的基类
 * 本基础类提供了常用的能力，各个业务的Controller都应继承本类，以获取这些能力。
 *
 * @author zhaolei
 * @version 2013-3-23
 */
public abstract class BaseController {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 管理后台基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    /**
     * 商家后台基础路径
     */
    @Value("${sellerPath}")
    protected String sellerPath;

    /**
     * 会员中心基础路径
     */
    @Value("${memberPath}")
    protected String memberPath;

    /**
     * 前台基础路径
     */
    @Value("${frontPath}")
    protected String frontPath;

    /**
     * sso基础路径
     */
    @Value("${ssoPath}")
    protected String ssoPath;

    /**
     * wap系统基础路径
     */
    @Value("${wapPath}")
    protected String wapPath;

    /**
     * upload系统基础路径
     */
    @Value("${uploadPath}")
    protected String uploadPath;

    /**
     * 前端URL后缀
     */
    @Value("${urlSuffix}")
    protected String urlSuffix;


    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    /**
     * 缓存接口
     */
    @Autowired
    protected ShopCache cache;

    /**
     * 用于控制菜单高亮
     */
    @Autowired
    MenuDao menuDao;

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(model, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
     */
    protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(redirectAttributes, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组，不传入此参数时，同@Valid注解验证
     * @return 验证成功：继续执行；验证失败：抛出异常跳转400页面。
     */
    protected void beanValidator(Object object, Class<?>... groups) {
        BeanValidators.validateWithException(validator, object, groups);
    }

    /**
     * 添加Model消息
     *
     * @param model
     * @param messages
     */
    protected void addMessage(Model model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("message", sb.toString());
    }

    /**
     * 添加Flash消息
     *
     * @param redirectAttributes
     * @param messages
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
//        StringBuilder sb = new StringBuilder();
//        for (String message : messages) {
//            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
//        }
//        redirectAttributes.addFlashAttribute("message", sb.toString());
        addMessage("message",redirectAttributes, messages);
    }
    /**
     * 添加Flash消息
     *
     * @param redirectAttributes
     * @param key
     * @param messages
     */
    protected void addMessage(String key, RedirectAttributes redirectAttributes,String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute(key, sb.toString());
    }

    /**
     * 向客户端返回JSON字符串
     *
     * @param response
     * @param object
     * @return
     */
    protected String renderString(HttpServletResponse response, Object object) {
        return renderString(response, JsonMapper.toJsonString(object), "application/json");
    }

    /**
     * 向客户端返回字符串
     *
     * @param response
     * @param string
     * @return
     */
    protected String renderString(HttpServletResponse response, String string, String type) {
        try {
            response.reset();
            response.setContentType(type);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, ValidationException.class})
    public String bindException() {
        return "error/400";
    }

    /**
     * 授权登录异常
     */
    @ExceptionHandler({AuthenticationException.class})
    public String authenticationException() {
        return "error/403";
    }

    /**
     * 初始化数据绑定
     * 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : text.trim());
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? DateUtils.formatDateTime((Date)value) : "";
//			}
        });
    }

    /**
     * 手动设置菜单高亮
     * 你传一个未级菜单的ID(一般是第三级)，自动找出二级、一级菜单的ID，并存放在Request的Attribute中，供 top.jsp\left.jsp使用。
     * 达到手动控制某一个菜单高亮的目标。
     * 90%的时候你不需要手动控制，因为MenuInterceptor类会自动控制菜单高亮的。
     * 只有10%的少数场景下，需要你手动控制，本工具就是手动控制菜单高亮的工具
     *
     * @param menuId  未级菜单ID
     */
    public void menuHighLighting(Long menuId){
        if(menuId==null){
            return ;
        }
        Menu menu= menuDao.selectById(menuId);//按ID来查找menu
        if (menu != null) {
            Long id = menu.getId();
            String parentIds = menu.getParentIds();
            // 组出菜单ID串
            String menuIds = null;
            menuIds = parentIds + id;
            // 菜单ID串：0,1,97,820,1123,2145。永远是0,1开头，一级菜单ID是97，二级菜单ID是820，三级菜单ID是1123，四级菜单ID是2145
            String[] arr = menuIds.split(",");
            if (arr.length >= 3) {
                String menu1id = arr[2];//一级菜单ID
                R.getRequest().setAttribute("menu1id", menu1id);//把menu1id放入上下文供jsp页面使用
            }
            if (arr.length >= 4) {
                String menu2id = arr[3];//二级菜单ID
                R.getRequest().setAttribute("menu2id", menu2id);//把menu2id放入上下文供jsp页面使用
            }
            if (arr.length >= 5) {
                String menu3id = arr[4];//三级菜单ID
                R.getRequest().setAttribute("menu3id", menu3id);//把menu3id放入上下文供jsp页面使用
            }
            if (arr.length >= 6) {
                String menu4id = arr[5];//四级菜单ID
                R.getRequest().setAttribute("menu4id", menu4id);//把menu4id放入上下文供jsp页面使用
            }
        }
    }

    /**
     * 手动设置菜单高亮
     * 你传一个未级菜单的menu_num(一般是第三级)，自动找出三级、二级、一级菜单的ID，并存放在Request的Attribute中，供 top.jsp\left.jsp使用。
     * 达到手动控制某一个菜单高亮的目标。
     * 90%的时候你不需要手动控制，因为MenuInterceptor类会自动控制菜单高亮的。
     * 只有10%的少数场景下，需要你手动控制，本工具就是手动控制菜单高亮的工具
     *
     * 注意：本方法已淘汰，请使用 menuHighLighting(Long menuId) 方法来替代。
     *
     * @param menuNum  未级菜单menu_num
     */
    @Deprecated
    public void menuHighLighting(String menuNum){
        if(StringUtils.isBlank(menuNum)){
            return ;
        }
        Wrapper wrapper=new Wrapper();
        wrapper.and("menu_num=",menuNum.trim());
        List<Menu> menuList= menuDao.selectByWhere(null,wrapper);//按menu_num来查找menu
        if(menuList.size()>0){
            Menu menu= menuList.get(0);
            Long menu3id=menu.getId();//得到了菜单的ID
            menuHighLighting(menu3id);
        }
    }
}
