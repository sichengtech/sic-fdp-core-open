/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.utils;

import com.sicheng.common.security.Digests;

/**
 * <p>标题: 用户中心工具类</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cailong
 * @date 2017年4月26日 下午5:35:01
 */
public class PasswordUtils {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;

    /**
     * 生成安全的密码
     *
     * @param plainPassword 明文密码
     * @param salt          盐
     * @return
     */
    public static String entryptPassword(String plainPassword, String salt) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt.getBytes(), HASH_INTERATIONS);
        return Encodes.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @param salt          盐
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password, String salt) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt.getBytes(), HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(hashPassword));
    }


    public static void main(String[] args) {
        String plainPassword = "123456";
        String password = "6b2b375a97a014e3bfa86bc59f538132559ae936";
        String salt = "CN6ardw3citwuavrLbLOl8bb69MabqKg";
        System.out.println(validatePassword(plainPassword, password, salt));
    }

}
