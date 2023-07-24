/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.fileStorage;

import com.sicheng.common.config.Global;
import com.sicheng.common.security.Cryptos;
import com.sicheng.common.security.MD5;
import com.sicheng.common.utils.StringUtils;

/**
 * <p>标题: AccessKey</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年8月12日 下午4:52:56
 */
public class AccessKey {

    /**
     * 生成 AccessKey
     * 取当前时间，并使用用户输入的密码进行加密
     *
     * @return
     */
    public static String generateAccessKey() {
        String password = Global.getConfig("filestorage.password");
        return generateAccessKey(password);
    }

    /**
     * 生成 AccessKey
     * 取当前时间，并使用用户输入的密码进行加密
     *
     * @param password 用户输入的密码
     * @return
     */
    public static String generateAccessKey(String password) {
        if (StringUtils.isBlank(password)) {
            throw new RuntimeException("缺少密码");
        }
        long time = System.currentTimeMillis();//当前时间毫秒数
        String pwd = MD5.encrypt(password);//把用户输入的密码转为128位（长度32）的标准密码
        String ciphertext = Cryptos.aesEncrypt(time + "", pwd);//加密
        return ciphertext;
    }

    /**
     * 验证AccessKey
     *
     * @param ciphertext 密文
     * @return true有效，false无效
     */
    public static boolean verification(String ciphertext) {
        String password = Global.getConfig("filestorage.password");
        return verification(ciphertext, password);
    }

    /**
     * 验证AccessKey
     *
     * @param ciphertext 密文
     * @param password   用户输入的密码
     * @return true有效，false无效
     */
    public static boolean verification(String ciphertext, String password) {
        if (StringUtils.isBlank(ciphertext)) {
            return false;
        }
        if (StringUtils.isBlank(password)) {
            return false;
        }
        try {
            String pwd = MD5.encrypt(password);//把用户输入的密码转为128位（长度32）的标准密码
            String text = Cryptos.aesDecrypt(ciphertext, pwd);

            long time_m = Long.valueOf(text);
            long time_current = System.currentTimeMillis();//当前时间毫秒数

            long m10 = 1000 * 60 * 30;//30分钟的毫秒数
            if (time_current - time_m >= m10) {
                //已过期
                return false;
            } else {
                //验证通过，合格
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] a) {
        for (int i = 0; i < 10; i++) {
            String password = "djei39f83";//IdGen.uuid();//用户的密码
            String ciphertext = AccessKey.generateAccessKey(password);
            System.out.println(ciphertext);
            boolean bl = AccessKey.verification(ciphertext, password);
            System.out.println(bl);
        }
    }

}
