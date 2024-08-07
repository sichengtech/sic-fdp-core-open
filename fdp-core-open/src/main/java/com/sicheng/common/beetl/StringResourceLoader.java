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
package com.sicheng.common.beetl;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.resource.StringTemplateResource;

/**
 * <p>标题: StringResourceLoader</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年9月11日 下午6:57:30
 */
public class StringResourceLoader implements ResourceLoader {
    String content;

    public StringResourceLoader(String content) {
        this.content = content;
    }

    /**
     * 根据key获取Resource
     *
     * @param key
     * @return
     */
    @Override
    public Resource getResource(String key) {
        if (content == null) {
            return null;
        }
        return new StringTemplateResource(content, this);
    }

    /**
     * 检测模板是否更改，每次渲染模板前，都需要调用此方法，所以此方法不能占用太多时间，否则会影响渲染功能
     *
     * @param key
     * @return
     */
    @Override
    public boolean isModified(Resource key) {
        return false;
    }

    @Override
    public boolean exist(String key) {
        return true;
    }

    /**
     * 关闭ResouceLoader，通常是GroupTemplate关闭的时候也关闭对应的ResourceLoader
     */
    @Override
    public void close() {

    }

    /**
     * 一些初始化方法
     *
     * @param gt
     */
    @Override
    public void init(GroupTemplate gt) {

    }

    /**
     * 用于include，layout等根据相对路径计算资源实际的位置.
     *
     * @param resource 当前资源
     * @param id
     * @return
     */
    @Override
    public String getResourceId(Resource resource, String id) {
        // 不需要计算相对路径
        return id;
    }

    /**
     * 得到资源加载器说明，用于获取不到资源的时候输出提示信息
     *
     * @return
     */
    public String getInfo() {
        return "";
    }
}