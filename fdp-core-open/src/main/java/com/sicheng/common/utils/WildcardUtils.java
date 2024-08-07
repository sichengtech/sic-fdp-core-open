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
package com.sicheng.common.utils;

/**
 * <p>
 * 标题: WildcardUtils
 * </p>
 * <p>
 * 描述: 通配符工具类
 * </p>
 * <p>
 * 实现以下通配符规则：
 * *匹配多个任意字符，?匹配一个任意字符 。
 * h?llo 匹配 hello、hallo、hxllo 等。
 * h*llo 匹配 hllo、heeeeello 等。
 * h[ae]llo 匹配 hello 、hallo ，但不匹配 hillo 。
 *
 * <p>
 * 公司: 思程科技 www.sicheng.net
 * </p>
 *
 * @author zhaolei
 * @date 2017年10月20日 下午6:06:33
 */
public class WildcardUtils {

    private WildcardUtils() {

    }

    /**
     * 实现以下通配符规则：
     * *匹配多个任意字符，?匹配一个任意字符 。
     * h?llo 匹配 hello、hallo、hxllo 等。
     * h*llo 匹配 hllo、heeeeello 等。
     * h[ae]llo 匹配 hello 、hallo ，但不匹配 hillo 。
     *
     * @param pattern
     * @param str
     * @return
     */
    public static boolean test(String pattern, String str) {
        String newPattern = toJavaPattern(pattern);
        return java.util.regex.Pattern.matches(newPattern, str);
    }

    /**
     * 通配符规则 转换为 正则表达式
     *
     * @param pattern
     * @return
     */
    private static String toJavaPattern(String pattern) {
        StringBuilder sbl = new StringBuilder();
        sbl.append("^");
        char[] metachar = {'$', '^', '(', ')', '{', '|', '+', '.'};
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean isMeta = false;
            for (int j = 0; j < metachar.length; j++) {
                if (ch == metachar[j]) {
                    sbl.append("\\");
                    sbl.append(ch);
                    isMeta = true;
                    break;
                }
            }
            if (!isMeta) {
                if (ch == '*') {
                    sbl.append(".*");
                } else if (ch == '?') {
                    sbl.append(".{1}");
                } else {
                    sbl.append(ch);
                }
            }
        }
        sbl.append("$");
        return sbl.toString();
    }
}