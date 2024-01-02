/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
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
