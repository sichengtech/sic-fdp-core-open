/*
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */

package com.sicheng.common.web;

import org.junit.Test;

/**
 * <p>标题: xxxx  </p>
 * <p>描述: xxxx  </p>
 * <p>使用示例: </P>
 * <pre>
 *    Window win = new Window(parent);//请作者手动完善此信息
 *    win.show();
 * </pre>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @version 2020-02-04 0:01
 *
 * <p>重要修改历史记录1: xxxx  。修改人：xx</p>
 * <p>重要修改历史记录2: xxxx  。修改人：xx</p>
 */
public class RTest {
    public static void main(String[] args) {
        String rootPath = R.getWebRoot();
        System.out.println("testGetWebRoot rootPath=" + rootPath);
    }

    @Test
    public void testGetWebRoot() {
        String rootPath = R.getWebRoot();
        System.out.println("testGetWebRoot rootPath=" + rootPath);
    }

}
