/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.beetl;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.misc.BeetlUtil;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.WebAppResourceLoader;

import java.io.File;
import java.io.Reader;
import java.util.Map;

/**
 * <p>
 * 父子资源加载器ShopResourceLoader
 * </p>
 * <p>
 * 描述: 优先从子加载器找模板，若找不到，再从父加载器找模板
 * </p>
 * <p>
 * 公司: 思程科技 www.sicheng.net
 * </p>
 *
 * @author zhaolei
 * @date 2017年6月16日 上午9:13:00
 */
public class ShopResourceLoader implements ResourceLoader {

    private String parentPath;// 父加载器的模板文件路径
    private String childPath;// 子加载器的模板文件路径
    private ResourceLoader parent = null;// 父加载器
    private ResourceLoader child = null;// 子加载器

    /**
     * 是否自动检查文件是否变动
     */
    private boolean autoCheck = false;

    static public String FILE_PATH = "file:";
    static public String CLASSPATH = "classpath:";

//	/**
//	 * 添加一个资源加载器
//	 * 
//	 * @param resourceLoader
//	 */
//	public void addParentResourceLoader(ResourceLoader resourceLoader) {
//		parent = resourceLoader;
//	}

//	/**
//	 * 添加一个资源加载器
//	 * 
//	 * @param resourceLoader
//	 */
//	public void addChildResourceLoader(ResourceLoader resourceLoader) {
//		child = resourceLoader;
//	}

    /**
     * 关闭ResouceLoader，通常是GroupTemplate关闭的时候也关闭对应的ResourceLoader
     */
    @Override
    public void close() {
        if (child != null) {
            child.close();
        }
        if (parent != null) {
            parent.close();
        }
    }

    /**
     * <p>
     * 判断模板文件是否存在
     * </p>
     *
     * @param key 模板文件的路径
     * @return
     * @see org.beetl.core.ResourceLoader#exist(java.lang.String)
     */
    @Override
    public boolean exist(String key) {
        boolean bl = false;
        if (child != null) {
            bl = child.exist(key);
            if (!bl) {
                if (parent != null) {
                    bl = parent.exist(key);
                }
            }
        }
        return bl;
    }

    /**
     * 根据key获取Resource
     *
     * @param key
     * @return
     */
    @Override
    public Resource getResource(String key) {
        if (child != null) {
            if (child.exist(key)) {
                return child.getResource(key);
            }
        }
        if (parent != null) {
            if (parent.exist(key)) {
                return parent.getResource(key);
            }
        }
        //如果上面两个资源加载器中都未能找到模板，返回一个具体展示异常功能的结果
        return new UnReachableResource(key, this);
    }

    private final static class UnReachableResource extends Resource {
        UnReachableResource(String key, ResourceLoader loader) {
            super(key, loader);
        }

        @Override
        public Reader openReader() {
            BeetlException be = new BeetlException(BeetlException.TEMPLATE_LOAD_ERROR, "父子加载器ShopResourceLoader未匹配路径:" + this.id);
            //be.resourceId = this.id; //使用beetl 2.7.15时本行代码有效。
            be.resource = this;  //升级到beetl 2.8.7，以上代码报错，所以写了本行代码。
            throw be;
        }

        @Override
        public boolean isModified() {
            return true;
        }
    }

    /**
     * 一些初始化方法
     *
     * @param gt
     */
    @Override
    public void init(GroupTemplate gt) {
        parentPath2 = parentPath;
        childPath2 = childPath;

//		ShopResourceLoader资源加载器支持的模板格式
//		一、Web应用根路径WebRoot目录: /WebRoot （默认）
//		二、绝对路径:file:D:\\template 
//		三、类路径:classpath:com/sicheng/template		

        if (parentPath != null && parentPath.startsWith(FILE_PATH)) {
            //从磁盘文件夹加载模板文件，这个文件夹一般在tomcat之外
            parent = new FileResourceLoader(parentPath.substring(FILE_PATH.length()));
        } else if (parentPath != null && parentPath.startsWith(CLASSPATH)) {
            //从classpath加载模板文件
            parent = new ClasspathResourceLoader(parentPath.substring(CLASSPATH.length()));
        } else {
            //从webroot加载模板文件
            String webRoot=BeetlUtil.getWebRoot();
            String p = webRoot + (isJoint(webRoot,parentPath)?File.separator:"") + parentPath;//"/views/front/default";
            parent = new WebAppResourceLoader(p);// "/views/front/default"
        }

        if (childPath != null && childPath.startsWith(FILE_PATH)) {
            //从磁盘文件夹加载模板文件，这个文件夹一般在tomcat之外
            child = new FileResourceLoader(childPath.substring(FILE_PATH.length()));
        } else if (childPath != null && childPath.startsWith(CLASSPATH)) {
            //从classpath加载模板文件
            child = new ClasspathResourceLoader(childPath.substring(CLASSPATH.length()));
        } else {
            //从webroot加载模板文件
            String webRoot=BeetlUtil.getWebRoot();
            String c = webRoot + (isJoint(webRoot,childPath)?File.separator:"")  + childPath;//"/views/front/demo";
            child = new WebAppResourceLoader(c);// "/views/front/qiche"
        }

        if (child != null) {
            child.init(gt);
        }
        if (parent != null) {
            parent.init(gt);
        }
        Map<String, String> resourceMap = gt.getConf().getResourceMap();
        this.autoCheck = Boolean.parseBoolean(resourceMap.get("autoCheck"));
    }

    @Override
    public boolean isModified(Resource key) {
        if (this.autoCheck) {
            return key.isModified();
        } else {
            return false;
        }
    }

    /**
     * 用于include，layout等根据相对路径计算资源实际的位置.
     *
     * @param resource 当前资源
     * @param resource
     * @param id
     * @return
     */
    @Override
    public String getResourceId(Resource resource, String id) {
        if (resource == null) {
            return id;
        }
        if (resource.getResourceLoader() == child) {
            return resource.getResourceLoader().getResourceId(resource, id);
        } else if (resource.getResourceLoader() == parent) {
            return resource.getResourceLoader().getResourceId(resource, id);
        } else {
            return id;
        }
    }

    /**
     * 得到资源加载器说明，用于获取不到资源的时候输出提示信息
     *
     * @return
     */
    @Override
    public String getInfo() {
        return "ShopResourceLoader(父子加载器)---子加载器:" + child.getInfo() + ",父加载器:" + parent.getInfo();
    }

    /**
     * @return the parentPath
     */
    public String getParentPath() {
        return parentPath;
    }

    /**
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * @return the childPath
     */
    public String getChildPath() {
        return childPath;
    }

    /**
     * @param childPath the childPath to set
     */
    public void setChildPath(String childPath) {
        this.childPath = childPath;
    }


    private static String parentPath2;// 保存一份静态的值,为FrontStaticFileFilter
    private static String childPath2;// 保存一份静态的值

    /**
     * 为FrontStaticFileFilter准备的专用方法
     *
     * @return the childPath
     */
    public static String getChildPath2() {
        return childPath2;
    }

    /**
     * 为FrontStaticFileFilter准备的专用方法
     *
     * @return the parentPath
     */
    public static String getParentPath2() {
        return parentPath2;
    }

    /**
     * 判断 webRoot和path之间，是否要拼接 File.separator
     * File.separator 是系统默认的文件分隔符号
     *
     * 前题：
     * 当webRoot = C:\dev-java\IdeaProjects\project-b2b2cShop\shop-web-front\target\front
     * 当path = /views/front/default
     *
     * 错误示例：
     * 没有判断直接强拼接 webRoot + File.separator + path 拼接好的路径如下：
     * C:\dev-java\IdeaProjects\project-b2b2cShop\shop-web-front\target\front\/views/front/default
     * 注意：front\/views 这里，多拼接了一个\符号。
     *
     * 正常的示例：
     * C:\dev-java\IdeaProjects\project-b2b2cShop\shop-web-front\target\front/views/front/default
     * 注意：front/views 这里，只有一个/符号。
     *
     * @param webRoot  前路径
     * @param path     后路径
     * @return  true表示要在中间拼接File.separator，false表示不要在中间拼接File.separator
     */
    private static boolean isJoint(String webRoot, String path){
        if(webRoot==null || path==null ){
            return false;
        }
        boolean b1=false;
        if(webRoot.endsWith("\\") || webRoot.endsWith("/")){
            b1=true;
        }
        boolean b2=false;
        if(path.startsWith("\\") || path.startsWith("/")){
            b2=true;
        }
        if(b1||b2){
            return false;
        }else{
            return true;
        }
    }
}
