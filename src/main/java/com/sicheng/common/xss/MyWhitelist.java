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

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

/**
 * <p>标题: MyWhitelist自定白名单</p>
 * <p>描述:
 * jsoup 是一款Java 的HTML解析器，可分析HTML内容。
 * jsoup默认的规则认为：
 * <img width='200' height='200' src='/upload/filestorage/15/54/92/85.jpg'></img>  中的 src属性
 * src='http://upload/filestorage/15/54/92/1285.jpg' 被认定是安全的
 * src='/upload/filestorage/15/54/92/1285.jpg'  被认定是不安全，会被过滤掉。这里不能满足我的业务要求，我希望这里被认定是安全的。
 * 所以写了自定义的MyWhitelist类，继承自Whitelist，修改了jsoup原有的规则，已经可以满足业务要求。
 * 如果以后“过滤白名单”有变化，请修改本类，已满足变化的业务要求。
 * </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年4月15日 上午11:00:44
 */
public class MyWhitelist extends Whitelist {

    /**
     * 自定义白名单（支持HTML5标签。）
     *
     * @Title:构造方法
     * @Description: 可在这里自定义白名单。
     * 在前端我使用的是百度编辑器，百度编辑器自带xss过滤白名单。
     * 本服务端的xss过滤白名单要与前端的 xss过滤白名单 一致，已一致。
     * 支持HTML5标签。
     */
    public MyWhitelist() {
        //添加“允许的”标签 "",
        addTags("a", "abbr", "address", "area", "article", "aside", "audio",
                "b", "bdi", "bdo", "big", "blockquote", "br",
                "caption", "center", "cite", "code", "col", "colgroup",
                "dd", "del", "details", "div", "dl", "dt",
                "em",
                "font", "footer",
                "h1", "h2", "h3", "h4", "h5", "h6", "header", "hr",
                "i", "img", "ins",
                "li",
                "mark", "nav",
                "ol",
                "p", "pre",
                "q",
                "s", "small", "span", "strike", "strong", "sub", "sup",
                "table", "tbody", "td", "tfoot", "th", "thead", "tr", "tt",
                "u", "ul",
                "video");

        //添加“允许的”属性，第一个参数是标签名称，其它的参数是本标签“允许的”属性
        addAttributes("a", "target", "href", "title");
        addAttributes("abbr", "title");
        addAttributes("area", "shape", "coords", "href", "alt");
        addAttributes("audio", "autoplay", "controls", "loop", "preload", "src");
        addAttributes("bdi", "dir");
        addAttributes("bdo", "dir");
        addAttributes("blockquote", "cite");
        addAttributes("col", "align", "valign", "span");
        addAttributes("colgroup", "align", "valign", "span");
        addAttributes("del", "datetime");
        addAttributes("details", "open");
        addAttributes("font", "color", "size", "face");
        addAttributes("img", "align", "alt", "src", "title", "_src", "loadingclass", "data-latex");
        addAttributes("ins", "datetime");
        addAttributes("ol", "start");
        addAttributes("q", "cite");
        addAttributes("table", "summary", "border", "align", "valign");
        addAttributes("tbody", "align", "valign");
        addAttributes("tfoot", "align", "valign");
        addAttributes("thead", "align", "valign");
        addAttributes("td", "abbr", "axis", "colspan", "rowspan", "align", "valign");
        addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "align", "valign");
        addAttributes("tr", "rowspan", "align", "valign");
        addAttributes("video", "src", "autoplay", "controls", "loop", "muted", "poster", "preload");//这是后加的

        //添加“允许的”属性，第一个参数是标签名称，其它的参数是本标签“允许的”属性，:all表示所有标签
        addAttributes(":all", "id");
        addAttributes(":all", "name");
        addAttributes(":all", "class");
        addAttributes(":all", "style");
        addAttributes(":all", "height");
        addAttributes(":all", "width");
        addAttributes(":all", "type");

        //添加“允许的”协议
        addProtocols("a", "href", "ftp", "http", "https", "mailto", "tel");//"tel"是后加的
        addProtocols("blockquote", "cite", "http", "https");
        addProtocols("cite", "cite", "http", "https");
        addProtocols("img", "src", "http", "https");
        addProtocols("q", "cite", "http", "https");

        //下面是 百度编辑器的xss过滤白名单，放在这里是为了供我参考
        //在前端我使用的是百度编辑器，百度编辑器自带xss过滤白名单。
        //服务端的xss过滤白名单要与前端的 xss过滤白名单 一致，才可达到最佳效果。
//		a:      ['target', 'href', 'title', 'class', 'style'],
//		abbr:   ['title', 'class', 'style'],
//		address: ['class', 'style'],
//		area:   ['shape', 'coords', 'href', 'alt'],
//		article: [],
//		aside:  [],
//		audio:  ['autoplay', 'controls', 'loop', 'preload', 'src', 'class', 'style'],
//		b:      ['class', 'style'],
//		bdi:    ['dir'],
//		bdo:    ['dir'],
//		big:    [],
//		blockquote: ['cite', 'class', 'style'],
//		br:     [],
//		caption: ['class', 'style'],
//		center: [],
//		cite:   [],
//		code:   ['class', 'style'],
//		col:    ['align', 'valign', 'span', 'width', 'class', 'style'],
//		colgroup: ['align', 'valign', 'span', 'width', 'class', 'style'],
//		dd:     ['class', 'style'],
//		del:    ['datetime'],
//		details: ['open'],
//		div:    ['class', 'style'],
//		dl:     ['class', 'style'],
//		dt:     ['class', 'style'],
//		em:     ['class', 'style'],
//		font:   ['color', 'size', 'face'],
//		footer: [],
//		h1:     ['class', 'style'],
//		h2:     ['class', 'style'],
//		h3:     ['class', 'style'],
//		h4:     ['class', 'style'],
//		h5:     ['class', 'style'],
//		h6:     ['class', 'style'],
//		header: [],
//		hr:     [],
//		i:      ['class', 'style'],
//		img:    ['src', 'alt', 'title', 'width', 'height', 'id', '_src', 'loadingclass', 'class', 'data-latex'],
//		ins:    ['datetime'],
//		li:     ['class', 'style'],
//		mark:   [],
//		nav:    [],
//		ol:     ['class', 'style'],
//		p:      ['class', 'style'],
//		pre:    ['class', 'style'],
//		s:      [],
//		section:[],
//		small:  [],
//		span:   ['class', 'style'],
//		sub:    ['class', 'style'],
//		sup:    ['class', 'style'],
//		strong: ['class', 'style'],
//		table:  ['width', 'border', 'align', 'valign', 'class', 'style'],
//		tbody:  ['align', 'valign', 'class', 'style'],
//		td:     ['width', 'rowspan', 'colspan', 'align', 'valign', 'class', 'style'],
//		tfoot:  ['align', 'valign', 'class', 'style'],
//		th:     ['width', 'rowspan', 'colspan', 'align', 'valign', 'class', 'style'],
//		thead:  ['align', 'valign', 'class', 'style'],
//		tr:     ['rowspan', 'align', 'valign', 'class', 'style'],
//		tt:     [],
//		u:      [],
//		ul:     ['class', 'style'],
//		video:  ['autoplay', 'controls', 'loop', 'preload', 'src', 'height', 'width', 'class', 'style']
    }

    /**
     * <p>描述: 自定义Attribute的安全过滤规则 </p>
     *
     * @param tagName html标签的名称
     * @param el      元素
     * @param attr    属性
     * @return
     * @see org.jsoup.safety.Whitelist#isSafeAttribute(java.lang.String, org.jsoup.nodes.Element, org.jsoup.nodes.Attribute)
     */
    @Override
    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        //1、执行父类的安全过滤规则，这已可满足99.9%的安全要求
        boolean bl = super.isSafeAttribute(tagName, el, attr);
        if (bl) {
            //2、如果被认定为安全，就返回true
            return bl;
        } else {
            //3、如果被认定为不安全，则执行我写的扩展逻辑（具体是什么逻辑请看类上的注释）
            if ("img".equals(tagName) && "src".equals(attr.getKey())) {
                String value = attr.getValue();
                if (value != null) {
                    // src='javascript:alert(123);' 要求被认定为不安全
                    if (value.toLowerCase().contains("javascript:")) {
                        return false;
                    }
                    //如果有新的“危险判断条件”，请在这里写正则来匹配。
                    // ...
                }
                // 其它的，都被认定为安全
                // <img width='200' height='200' src='/upload/filestorage/15/54/92/85.jpg'></img> 被认定为安全
                // <img width='300' height='300' src='data:;base64,I48RI49388948G94MV9XMW9'></img> 被认定为安全
                return true;
            }
        }
        return bl;
    }
}