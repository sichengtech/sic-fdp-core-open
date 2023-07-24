/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.utils;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * <p>标题: WildcardUtilsTest</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2018年2月17日 下午8:32:02
 */
public class WildcardUtilsTest {
    @Test
    public void test1() {
        //以下结果为true
        testInner("*", "key");
        testInner("h?llo", "hello");
        testInner("h*llo", "heeeeello");
        testInner("h[ae]llo", "hello");
        testInner("h[ae]llo", "hallo");

        testInner("*", "toto");
        testInner("***", "toto");
        testInner("*.java", "toto.java");
        testInner("a*c", "abbbbbccccc");
        testInner("abc*xyz", "abcxxxyz");
        testInner("*xyz", "abcxxxyz");
        testInner("abc**xyz", "abcxxxyz");
        testInner("abc**x", "abcxxx");
        testInner("*a*b*c**x", "aaabcxxx");
        testInner("abc*x*yz", "abcxxxyz");
        testInner("abc*x*yz*", "abcxxxyz");
        testInner("abc*xyz", "abcxyxyz");
        testInner("*LogServerInterface*.java", "_LogServerInterfaceImpl.java");
        testInner("a*b*c*x*yf*ze", "aabbccxxxeeyfze");
        testInner("a*b*c*x*yf*z*", "aabbccxxxeeyffz");
    }

    @Test
    public void test2() {
        //以下结果为false
        testInnerFalse("h[ae]llo", "hillo");
        testInnerFalse("toto.java", "tutu.java");
        testInnerFalse("12345", "1234");
        testInnerFalse("1234", "12345");
        testInnerFalse("*f", "");
        testInnerFalse("*.java", "toto.");
        testInnerFalse("*.java", "toto.jav");
        testInnerFalse("abc*", "");
        testInnerFalse("a*b*c*x*yf*zze", "aabbccxxxeeyffz");
        testInnerFalse("a*b*c*x*yf*ze", "aabbccxxxeeyffz");
    }

    private static void testInner(String pattern, String str) {
        boolean bl = WildcardUtils.test(pattern, str);
        TestCase.assertEquals(true, bl);
        System.out.println(pattern + " : " + str + " =>> " + bl);
    }

    private static void testInnerFalse(String pattern, String str) {
        boolean bl = WildcardUtils.test(pattern, str);
        TestCase.assertEquals(false, bl);
        System.out.println(pattern + " : " + str + " =>> " + bl);
    }
}
