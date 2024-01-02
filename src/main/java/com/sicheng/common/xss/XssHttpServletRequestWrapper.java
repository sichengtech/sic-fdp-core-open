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
package com.sicheng.common.xss;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>标题: XssHttpServletRequestWrapper 对“危险字符”做转义</p>
 * <p>描述:
 * XssFilter 过滤器 把数据 转交给XssHttpServletRequestWrapper.java处理，转义危险字符。
 * 关键是XssHttpServletRequestWrapper的实现方式，继承servlet的HttpServletRequestWrapper，并重写相应的几个Request“取值”的方法，在取值时对“危险字符”做转义，达到防止xss攻击的目标。
 * 使用XssFilter过滤器，对所有表单提交上来的数据，都会自动处理转义危险字符，存储到数据库的中的数据是无危险字符的安全数据。
 * 主要原理是用到commons-lang3-3.1.jar这个包的org.apache.commons.lang3.StringEscapeUtils.escapeHtml4()这个方法转义危险字符<、>、’、“、&等。
 * 是与XssFilter配合使用的。
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2016年8月1日 上午12:37:53
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        //1%走这里，是针对有"If-None-Match"请求头的特殊处理。
        //请求头示例：If-None-Match: W/"43311-1685218978000"，注意它的值中带有引号，就是这个引号遇到“防止xss攻击程序”无法正常工作了。
        //遇到问题的现象描述：浏览器请求某个css文件，第一次下返回200，第二次返回400，很有过滤。都因为第二次请求头中有If-None-Match的原因。
        //参考：https://blog.csdn.net/gyr962/article/details/122497892
        //解决方法：遇到"If-None-Match"请求头，对他的值不做转义处理。
        if("If-None-Match".equals(name)){
            return super.getHeader(name) != null ? super.getHeader(name).replace("&quot;","\"") : null;
        }
        //99%走这里，使用escapeHtml4转义，达到过滤危险字符的目标。这是“防止xss攻击程序”要求的。
        return StringEscapeUtils.escapeHtml4(super.getHeader(name));
    }

    @Override
    public String getQueryString() {
        //赵磊注释 2016-8-13 (花了两天时间才找到原因)
        //我在本项目中，重点使用了redirectAttributes.addFlashAttribute("message", "保存成功");
        //如果这里使用StringEscapeUtils工具对super.getQueryString()的值进行了处理，a=1&b=2  会被处理成  a=1&amp;b=2
        //就会干扰AbstractFlashMapManager的正常工作，AbstractFlashMapManager中进行匹配，既要匹配url,也要匹配参数的名字和数量。
        //显然“b”和“amp;b”是无法匹配的，就会导致无法取出message参数。
        //所以getQueryString()方法要保持原样，不可以被StringEscapeUtils工具转码。
        //return StringEscapeUtils.escapeHtml4(super.getQueryString());
        return super.getQueryString();
    }

    @Override
    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml4(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapseValues = new String[length];
            for (int i = 0; i < length; i++) {
                escapseValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
            }
            return escapseValues;
        }
        return super.getParameterValues(name);
    }

    public static void main(String[] args) {
        String html = " ' \"  <  > & 中文";
        String s = StringEscapeUtils.escapeHtml4(html);
        System.out.println(s);
        // 输出的是  ' &quot;  &lt;  &gt; &amp;    "可能会造成破坏的符号"都被转义了，单引号未转义，中文未被转义，总体满意。

        String s2 = StringEscapeUtils.unescapeHtml4(s);
        System.out.println(s2);
        // 输出  ' "  <  > &
    }

}