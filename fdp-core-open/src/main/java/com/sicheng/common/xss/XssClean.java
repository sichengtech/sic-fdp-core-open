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
package com.sicheng.common.xss;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>标题: XssClean是一个工具，它可依据MyWhitelist白名单，过滤掉html中的有xss攻击危险代码</p>
 * <p>
 * 对于富文本编辑器防止XSS
 * 富文本编辑器，提交上来的内容允许包含大段的html代码，其中就会包含< >符号。不能再使用上一步的“转义危险字符”方案来处理了。
 * 解决方案：使用Jsoup的白名单过滤html代码,消除不受信任的HTML 。关于Jsoup请看文档：Jsoup的白名单\自定HTML白名单
 * 工具类是：com.sicheng.common.xss.XssClean
 * 工具使用方法是：String safe=XssClean.clean(unsafe);
 * 操作步骤：
 * StringEscapeUtils.unescapeHtml4(s);  转回来（还原，把已经被转义的HTML还原）
 * String safe=XssClean.clean(unsafe);  按白名单来去除不受信任的HTML
 *
 * <p>
 * 问题举例
 * 在做网站的时候，经常会提供用户评论的功能。有些不怀好意的用户，会把一些有攻击性的脚本发到评论内容中，
 * 而这些脚本可能会破坏整个页面的行为，更严重的是获取一些机要信息，此时需要清理该HTML，以避免跨站脚本cross-site scripting攻击（XSS）。
 * 方法
 * 使用jsoup HTML Cleaner 方法进行清除，但需要指定一个可配置的 Whitelist。
 * 示例
 * {@code
 * String unsafe ="<p><a href='http://example.com/' onclick='stealCookies()'>Link</a></p>";
 * String safe = Jsoup.clean(unsafe, Whitelist.basic());
 * // now: <p><a href="http://example.com/" rel="nofollow">Link</a></p>
 * }
 * <p>
 * jsoup提供了一系列的Whitelist基本配置，能够满足大多数要求；但如有必要，也可以进行修改，不过要小心。
 * 这个cleaner非常好用不仅可以避免XSS攻击，还可以限制用户可以输入的标签范围。
 * <p>
 * 五种基本白名单
 * Whitelist.none() 这个白名单只允许文本节点：所有HTML将被过滤。
 * Whitelist.simpleText() 这个白名单只允许简单的文本格式：b, em, i, strong, u。所有其他HTML（标签和属性）都将被删除。
 * Whitelist.basic() 这个白名单允许更全面的文本节点：a, b, blockquote, br, cite, code, dd, dl, dt, em, i, li, ol, p, pre, q, small, span, strike, strong, sub, sup, u, ul。和适当的属性。链接可以指向HTTP，HTTPS，FTP，邮件，有一个强制rel= nofollow属性。
 * Whitelist.basicWithImages() 这个白名单与basic()相同，又加上了IMG标签，可有适当的属性，src可指向HTTP或HTTPS。
 * Whitelist.relaxed() 这个白名单允许全方位的文本和HTML标签：a, b, blockquote, br, caption, cite, code, col, colgroup, dd, div, dl, dt, em, h1, h2, h3, h4, h5, h6, i, img, li, ol, p, pre, q, small, span, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul
 * 链接没有强制rel= nofollow属性，但是你可以添加，如果需要的话。
 * <p>
 * 2017-4-15 赵磊补充
 * jsoup提供的“五种基本白名单”未能满足业务的要求，所有编写了自定义的白名单类MyWhitelist
 *
 * @author zhaolei
 */
public class XssClean {
    public static final Logger LOG = LoggerFactory.getLogger(XssClean.class);

    /**
     * 使用白名单，过滤掉html中的有xss攻击危险代码
     *
     * @param unsafeHtml 有xss攻击危险代码的html代码
     * @return 干净的html代码
     */
    public static String clean(String unsafeHtml) {
        XssClean xss = new XssClean();
        String safeHtml = xss.baseClean(unsafeHtml);
        return safeHtml;
    }

    /**
     * 使用白名单，过滤掉html中的有xss攻击危险代码
     *
     * @param unsafeHtml 有xss攻击危险代码的html代码
     * @return 干净的html代码
     */
    private String baseClean(String unsafeHtml) {
        // 构造白名单，使用自定义的MyWhitelist白名单类
        MyWhitelist whitelist = new MyWhitelist();
        // 执行白名单过滤
        return Jsoup.clean(unsafeHtml, whitelist);
    }

    public static void main(String[] input) {
        String unsafe = "<p id='abc' class='c1' name='n2' >"//本行的属性都应当被保留
                + "<a href='http://example.com/' onclick='stealCookies()'>Link</a>" //本行的js代码应被过滤掉
                + "<img width='100' height='100' src='javascript:alert(123);'></img>"  //本行的js代码应被过滤掉
                + "<script type='text/javascript'>alert('js代码执行了');</script>"  //本行的js代码应被过滤掉
                + "<img width='200' height='200' src='/upload/filestorage/15/54/92/85.jpg'></img>"//本行的属性都应当被保留
                + "<img width='300' height='300' src='data:;base64,I48RI49388948G94MV9XMW9'></img>"//本行的属性都应当被保留
                + "<input type='text' name='user_url' />"  //input是表单输入标签，应被过滤掉
                + "<video src='xxx' width='400' height='400'></video>" //测试html5标签
                + "</p>";

        String safe = XssClean.clean(unsafe);
        System.out.println(safe);
        // now: <p><a href="http://example.com/" rel="nofollow">Link</a></p>
    }
}
