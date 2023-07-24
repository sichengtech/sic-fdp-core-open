/*
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */

package com.sicheng.common.config;

import com.sicheng.common.web.R;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>标题: Global类的测试测试  </p>
 * @author zhaolei
 * @version 2019-11-29 18:59
 *
 */
public class GlobalTest {

    @Test
    public void test_getConfig() {
        //test.properties是为GlobalTest单元测试准备的测试专用文件,在resources目录中。
        String coreConfigFilePath="test.properties";
        Global.setCoreConfigFilename(coreConfigFilePath);

        Assert.assertEquals(Global.getConfig("jdbc.type"), "mysql");
        Assert.assertEquals(Global.getConfig("productName"), "思程B6-dev");
        Assert.assertEquals(Global.getConfig("empty"), "");
    }

}
