package com.sicheng.common.utils;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

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
