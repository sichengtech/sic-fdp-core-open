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
package com.sicheng.common.config;

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
