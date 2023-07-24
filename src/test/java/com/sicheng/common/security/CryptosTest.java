/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
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
