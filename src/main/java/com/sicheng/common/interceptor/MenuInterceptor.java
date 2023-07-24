/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.interceptor;

import com.sicheng.admin.sys.dao.MenuDao;
import com.sicheng.admin.sys.entity.Menu;
import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.persistence.BaseEntity;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.common.utils.StringUtils;
import com.sicheng.common.web.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 菜单高亮拦截器
 * <p>
 * 当URL中没有menuId885参数时，将按URL字符串来匹配。 90%
 * 当URL中有明确的menuId885参数时，会按菜单ID来处理菜单高亮。menuId885是可以为空的。10%
 *
 * @author zhaolei
 * @version 2022-6-1
 */
public class MenuInterceptor implements HandlerInterceptor {
    static String MENU_ID_KEY = "menuId885";//key
    static String MENU_HIGHLIGHT_CACHE_KEY = "menu_highlight_admin_";//菜单高亮缓存key的前缀【admin子系统专用】
    static long CACHE_TIME = 1200;//缓存有效期，单位秒

    @Autowired
    ShopCache cache;

    /**
     * 用于取得 菜单高亮缓存key的前缀
     * 问：为什么要写这个方法？
     * 答：本方法是为了被子类重写，从而实现子类可使用不同的 菜单高亮缓存key的前缀 的效果
     * @return 菜单高亮缓存key的前缀
     */
    protected String getMenuHighlightCacheKey(){
        return MENU_HIGHLIGHT_CACHE_KEY;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //请求的路径
        String url = this.getRequestUrl(request);

        ///////////////////////
        // 得到 menuIds 菜单ID串
        ///////////////////////

        // 当URL中有明确的menuId885参数时，会走这里，按菜单ID来处理菜单高亮。注意menuId885参数是可以为空的，key叫menuId885是为是尽量避免与业务参数重名。
        // 优点：按菜单ID来处理菜单高亮是最为精准的。
        // 缺点：开发人员要在URL中要携带menuId885参数，稍显繁琐。
        // 总结：约10%会走这里，比如当遇到复杂场景，开发人员介入在URL中要携带menuId885参数，就可展示精准的控制手法解决菜单高亮问题。
        // 使用菜单的多层父ID做为菜单ID串。生成菜单ID串的逻辑是： "文章管理"菜单的父ID是"0,1,97,820,",菜单ID是"1123",生成的菜单ID串是"0,1,97,820,1123"
        // 每个菜单或按钮的url带上菜单ID串，比如 ：sys/sysSmsLog/list.do?menuId885=0,1,97,820,1123，就可控制菜单高亮了。
        String menuIds = request.getParameter(MENU_ID_KEY);
        if (StringUtils.isBlank(menuIds)) {
            ////////////////////////////////////////////
            // 当URL中没有携带menuId885参数时，会走这里。将按URL字符串来匹配。
            // 优点：简单，开发人员不需要做什么工作，默认这是此逻辑。
            // 缺点：按URL字符串来匹配不是100%精准，因为url多多少少会发生变化。大多是简单场景是精准的。
            // 总结：约90%会走这里，因为大多数时候是一般简单场景，默认按URL字符串来匹配来可满足大多简单场景。
            ///////////////////////////////////////////

            //第一次，根据请求的URL，计算出menuIds
            menuIds = findMenuIds(url);
            if (StringUtils.isBlank(menuIds)) {
                String sessionId = request.getSession().getId();
                String key = getMenuHighlightCacheKey() + sessionId;
                //从缓存里取url
                String catchUrl = (String) cache.get(key);
                if(StringUtils.isNotBlank(catchUrl)){
                    //第二次，根据缓存的URL，计算出menuIds
                    menuIds = findMenuIds(catchUrl);
                }
            }else{
                // 把这个URL缓存起来,供URL配置不上时，就使用这前缓存起来的URL。因为就在刚刚之前使用过所以应该是临近的关系，可以做为菜单高亮的依据。
                // 问：为什么会匹配不上？
                // 答：一般能匹配上的都是/cms/article/list.do这种列表页的URL的格式，而像/cms/article/form.do?id=1285这种编辑页的URL都匹配不上。
                // 问：谁和谁匹配？
                // 答：本次请求的URL与“系统”--“菜单管理”中事先配置好的菜单的URL，
                // 比如：
                // 本次请求的URL格式是：“/cms/article/form.do”
                // “菜单管理”中事先配置好的菜单的URL是“/cms/article/form.do?id=1285”这种格式。
                // 就匹配不上。
                String sessionId = request.getSession().getId();
                String key = getMenuHighlightCacheKey() + sessionId;
                cache.put(key, url, CACHE_TIME);//menu != null时写入缓存
            }
        }
        if (StringUtils.isNotBlank(menuIds)) {
            // 菜单ID串的格式有可能是逗号结尾的，比如"0,1,97,820,1123,"  ，这里把结尾处的逗号裁掉。
            if (menuIds.endsWith(",")) {
                menuIds = menuIds.substring(0, menuIds.length() - 1);
            }
            // 菜单ID串：0,1,97,820,1123,2145。永远是0,1开头，一级菜单ID是97，二级菜单ID是820，三级菜单ID是1123，四级菜单ID是2145
            String[] arr = menuIds.split(",");
            if (arr.length >= 3) {
                String menu1id = arr[2];//一级菜单ID
                request.setAttribute("menu1id", menu1id);//把menu1id放入上下文供jsp页面使用
            }
            if (arr.length >= 4) {
                String menu2id = arr[3];//二级菜单ID
                request.setAttribute("menu2id", menu2id);//把menu2id放入上下文供jsp页面使用
            }
            if (arr.length >= 5) {
                String menu3id = arr[4];//三级菜单ID
                request.setAttribute("menu3id", menu3id);//把menu3id放入上下文供jsp页面使用
            }
            if (arr.length >= 6) {
                String menu4id = arr[5];//四级菜单ID
                request.setAttribute("menu4id", menu4id);//把menu4id放入上下文供jsp页面使用
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

    /**
     * 从request对象中取出请求的URL
     * 兼容Tomcat与WebSphere
     *
     * @param request
     * @return
     */
    private String getRequestUrl(HttpServletRequest request) {
        //----seller---
        //前提条件：
        //程序部署到tomcat时设置的contextPath:/888 【这使用888】
        //<url-pattern>/seller/*</url-pattern> 【注意这是使用的是/seller/*】
        //请求URL：http://localhost:8080/seller/index.htm?a=seller
        //结果：
        //request.getServletPath()：/seller 【这是居然是/seller？反常阿】 【我的shop商城就是这种情况】
        //request.getContextPath()：/888
        //request.getQueryString()：a=seller
        //request.getRequestURI()：/888/seller/index.htm        【发现：/888/seller/index.htm  减去 /888 ，可得到正确的结果  】
        //request.getRequestURL().toString()：http://localhost:8080/888/seller/index.htm
        //request.getPathInfo()：/index.htm 【这里居然非空？反常阿】

        // 计算出请求的url
        String servletPath = request.getServletPath();// 在tomcat下运行OK
        String pathInfo = request.getPathInfo();// 在WebSphere下运行OK

        if(StringUtils.isNotBlank(servletPath) && StringUtils.isBlank(pathInfo) ){
            return servletPath;
        }
        if(StringUtils.isBlank(servletPath) && StringUtils.isNotBlank(pathInfo)){
            return pathInfo;
        }
        if(StringUtils.isNotBlank(servletPath) && StringUtils.isNotBlank(pathInfo)){
            String uri=request.getRequestURI();
            String contextPath=request.getContextPath();
            //【发现：/888/seller/index.htm  减去 /888 ，可得到正确的结果  】
            return  StringUtils.substring(uri, contextPath.length());
        }
        return servletPath;
    }

    /**
     * 根据请求的URL，计算出menuIds菜单ID串
     * 提示：子类可重写本方法，实现从其它的菜单表查菜单数据。从而增加了适应性，适应复杂系统有多套菜单的情况。
     *
     * @param url     请求的URL
     * @return menuIds 菜单ID串
     */
    public String findMenuIds(String url) {
        String menuIds = null;
        Menu menu =null;
        Wrapper wrapper = new Wrapper();
        wrapper.and("href = ", url);//按URL来查询
        wrapper.and("del_flag = ", BaseEntity.DEL_FLAG_NORMAL);//非删除状态的
        //此menuDao是管理后台的，对应查sys_menu表
        MenuDao menuDao= SpringContextHolder.getBean(MenuDao.class);
        List<Menu> list= menuDao.selectByWhere(null,wrapper);//按URL来查找menu
        if(list.size()>0){
            menu=list.get(0);
        }
        if (menu != null) {
            Long id = menu.getId();
            String parentIds = menu.getParentIds();
            // 组出菜单ID串
            // 菜单ID串：0,1,97,820,1123。永远是0,1开头，一级菜单ID是97，二级菜单ID是820，三级菜单ID是1123，
            menuIds = parentIds + id;
        }
        return menuIds;
    }
}
