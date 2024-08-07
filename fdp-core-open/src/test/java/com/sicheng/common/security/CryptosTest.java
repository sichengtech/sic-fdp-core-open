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
package com.sicheng.common.security;

import org.junit.Test;

/**
 * <p>标题: CryptosTest</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年3月23日 下午3:53:00
 */
public class CryptosTest {
    /**
     * 生成AES密钥
     */
    @Test
    public void test_generateAesKeyString() {
        String s = Cryptos.generateAesKeyString();
        System.out.println(s);
    }

    /**
     * 使用AES加密原始字符串.
     */
    @Test
    public void test_aesEncrypt() {
        String txt = "你好，这是秘密";//明文
        String pwd = "547d74dbc5a46488f0edc8f90d1ee8b8";//密码
        String s = Cryptos.aesEncrypt(txt, pwd);
        System.out.println(s);
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     */
    @Test
    public void test_aesDecrypt() {
        String txt = "776b161af41a5c102f683c5469933f53fc5dec046c6f5137c0073cbc89ec1c26";//密文
        String pwd = "547d74dbc5a46488f0edc8f90d1ee8b8";//密码
        String s = Cryptos.aesDecrypt(txt, pwd);
        System.out.println(s);
    }

}
