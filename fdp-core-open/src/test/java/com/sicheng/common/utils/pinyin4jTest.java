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

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * <p>标题: WildcardUtilsTest</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2018年2月17日 下午8:32:02
 */
public class pinyin4jTest {

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        Pinyin4j pinyin4j = new Pinyin4j();
        String first1 = pinyin4j.toPinYinUppercase("颐和园");
        String first2 = pinyin4j.toPinYinUppercase("颐和园", "**");
        String first3 = pinyin4j.toPinYinLowercase("颐和园");
        String first4 = pinyin4j.toPinYinLowercase("颐和园", "**");
        String first5 = pinyin4j.toPinYinUppercaseInitials("颐和园");
        String first6 = pinyin4j.toPinYinLowercaseInitials("颐和园");
        System.out.println(first1);        //输出结果：YHY
        System.out.println(first2);        //输出结果：Y**H**Y
        System.out.println(first3);        //输出结果：yhy
        System.out.println(first4);        //输出结果：y**h**y
        System.out.println(first5);        //输出结果：Y
        System.out.println(first6);        //输出结果：y
    }

}
