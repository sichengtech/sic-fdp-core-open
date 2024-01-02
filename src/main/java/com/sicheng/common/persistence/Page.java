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
package com.sicheng.common.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sicheng.common.config.Global;
import com.sicheng.common.utils.CookieUtils;
import com.sicheng.common.web.R;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>标题: 分页类</p>
 * <p>描述: 是分页的核心类</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 * <p>
 * Page 分页的其特性
 * pageSize=-1 表示不分页
 * count=-1 表示不查询总数，可用于提高性能,但不能显示“总记录数”，超出后会查不出数据。适合海量数据的分页，如有100万条数据，页面上只有一个“下一页”按钮，用户一直点就一直翻页。直到返回空数据时停止。
 * count>0 表示已通过其它途径获得了conut总条数，不再需要再查询总条数，节省性能。适海量数据的分页，如有100万条数据，用户不可能从第1页翻到最后一页，可以只让用户查前100页(2000条)。
 * </p>
 *
 * @param <T> 数据实体类的类型
 * @author zhaolei
 * @version 2017年1月21日 上午11:44:08
 */
public class Page<T> {
    static public String PAGESIZE = "pageSize";    // 常量
    static public String PAGESIZE_COOKIE = "fdp_pageSize";    // 常量,pageSize存储在cookie中使用的key,见page.jsp
    static public String PAGENO = "pageNo";        // 常量
    static public String ENCODE = "UTF-8";        // 常量，编码，从request中取参数时可能会使用

    private long count;                            // 总记录数，设置为“2000”表示只查询前2000条，若每页20条只查100页，适用于海量数据
    private int pageSize = 20;                  // 每页记录数  -1表示不分页
    private int pageNo = 1;                    // 当前页码
    private String pageURL;                    // 分页导航的URL+查询参数

    private int first;                            // 首页索引
    private int last;                            // 尾页索引，总页数getTotalPage()
    private int prev;                            // 上一页索引
    private int next;                            // 下一页索引
    private boolean firstPage;                    // 是否是第一页
    private boolean lastPage;                    // 是否是最后一页
    private boolean beyondPage;                // 输入的页码超过总页数

    private int length = 8;                        // 分类导航条的长度
    private int slider = 1;                        // 前后显示页面长度

    private String orderBy = "";                // 标准查询有效， 实例： updatedate desc, name asc
    private String message = "";                // 设置提示消息，显示在“共n条”之后
    private List<T> list = new ArrayList<T>();    // 本页数据对象列表,是查询出来的一页数据

    /**
     * 创建一个默认分页对象（常用）
     * 从request中获取分页数据，来初始化一个page对象
     *
     * @return Page 分页对象
     */
    public static <T> Page<T> newPage() {
        HttpServletRequest request = R.getRequest();// 分页需要request
        HttpServletResponse response = R.getResponse();// 分页有可能需要response，就是用于写cookie
        Page<T> page = new Page<T>(request, response);// 创建分页对象
        return page;
    }

    /**
     * 构造方法
     *
     * @param request  传递 repage 参数，来记住页码
     * @param response 用于设置 Cookie，记住页码
     */
    public Page(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, Global.getConfigInteger("page.pageSize", 20));
    }

    /**
     * 构造方法
     *
     * @param request         传递 repage 参数，记住页码,   2016-8-13 赵磊修改：使用RepageInterceptor来实现，替换cookie实现
     * @param response        用于设置 Cookie，记住页码
     * @param defaultPageSize 默认分页大小，如果传递 -1 则为不分页，返回所有数据
     */
    public Page(HttpServletRequest request, HttpServletResponse response, int defaultPageSize) {
        // 设置页码参数
        String no = request.getParameter(PAGENO);
        if (StringUtils.isNumeric(no)) {
            this.setPageNo(Integer.parseInt(no));
        }
        // 设置页面大小参数
        String size = request.getParameter(PAGESIZE);
        if (StringUtils.isNumeric(size)) {
            this.setPageSize(Integer.parseInt(size));
        } else {
            //从cookie中取了pageSize
            //cookie的path属性实现很好的隔离
            //每个页面都有自己的url，就是不同的path，cookie会存储到这个path中，不会全站互相干扰
            size = CookieUtils.getCookie(request, PAGESIZE_COOKIE);
            if (StringUtils.isNumeric(size)) {
                this.setPageSize(Integer.parseInt(size));
            } else {
                this.setPageSize(defaultPageSize);
            }
        }

        // 设置排序参数
        String orderBy = request.getParameter("orderBy");
        if (StringUtils.isNotBlank(orderBy)) {
            this.setOrderBy(orderBy);
        }

        // 构造分页导航的URL，包含查询参数
        // 用于点击“下一页”时，能带参数进行查询
        buildPageURL(request);

        //initialize();//这里一定不能执行initialize()，会覆盖页面码，有单元测试为证
    }

    /**
     * 构造方法
     */
    public Page() {
        initialize();
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     */
    public Page(int pageNo, int pageSize) {
//        this(pageNo, pageSize, 0);
        this.setPageSize(pageSize);
        this.setPageNo(pageNo);
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     * @param count    数据条数
     */
    public Page(int pageNo, int pageSize, long count) {
//        this(pageNo, pageSize, count, new ArrayList<T>());
        this.setPageSize(pageSize);
        this.setPageNo(pageNo);
        this.setCount(count);// setCount()方法中会执行initialize();
    }

    /**
     * 构造方法
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     * @param count    数据条数
     * @param list     本页数据对象列表
     */
    public Page(int pageNo, int pageSize, long count, List<T> list) {
        this.list = list;
        this.setPageSize(pageSize);
        this.setPageNo(pageNo);
        this.setCount(count);// setCount()方法中会执行initialize();
    }

    /**
     * 初始化参数
     */
    public void initialize() {
        this.first = 1;
        this.last = (int) (count / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);
        if (this.count % this.pageSize != 0 || this.last == 0) {
            this.last++;
        }
        if (this.last < this.first) {
            this.last = this.first;
        }

        //处理是否超出，要放在靠前的位置，放在pageNo被改变之前
        //标记为页码未超出
        this.beyondPage = this.pageNo > this.last;//标记为页码超出

        //处理firstPage
        if (this.pageNo <= 1) {
            this.pageNo = this.first;
            this.firstPage = true;
        } else {
            this.firstPage = false;
        }
        //处理lastPage
        if (this.pageNo >= this.last) {
            this.pageNo = this.last;
            this.lastPage = true;
        } else {
            this.lastPage = false;
        }
        //处理next
        if (this.pageNo < this.last - 1) {
            this.next = this.pageNo + 1;
        } else {
            this.next = this.last;
        }
        //处理prev
        if (this.pageNo > 1) {
            this.prev = this.pageNo - 1;
        } else {
            this.prev = this.first;
        }
        // 如果当前页小于首页
        if (this.pageNo < this.first) {
            this.pageNo = this.first;
        }
        // 如果当前页大于尾页
        if (this.pageNo > this.last) {
            this.pageNo = this.last;
        }
    }

    /**
     * 构造分页导航的URL，包含查询参数 用于点击“下一页”时，能带参数进行查询
     *
     * @param request HttpServletRequest
     */
    private void buildPageURL(HttpServletRequest request) {
        String sPageURLPara = buildParameter(request);
        if (sPageURLPara.length() > 0) {
            pageURL = "?" + sPageURLPara.substring(1) + "&";
        } else {
            pageURL = "?";
        }
        pageURL = request.getRequestURI() + pageURL + "pageNo=";
    }

    /**
     * 从HttpServletRequest取参数，刨除pageNo参数（页码）
     *
     * @param request HttpServletRequest
     * @return 参数的字符串形式，如 &a=1&b=2
     */
    @SuppressWarnings("rawtypes")
    private String buildParameter(HttpServletRequest request) {
        Map map = request.getParameterMap();
        Set set = map.keySet();
        Iterator it = set.iterator();
        StringBuilder sbf = new StringBuilder();
        while (it.hasNext()) {
            String key = (String) it.next();
            if ((!key.equals(PAGENO)) && (!key.equals(PAGESIZE))) {
                String[] values = (String[]) map.get(key);
                for (String v : values) {
                    sbf.append("&");
                    sbf.append(key);
                    sbf.append("=");
                    if (request.getCharacterEncoding() == null) {
                        // 如果request没设置编码，就要解码
                        try {
                            sbf.append(new String(v.getBytes(StandardCharsets.ISO_8859_1), ENCODE));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 如果request已设置编码，直接取值就OK
                        sbf.append(v);
                    }
                }
            }
        }

        // 若有多个pageSize参数，只取最后一个，只拼接一次
        // 一般情况pageSize参数只会有一个
        String[] pageSize_value = request.getParameterValues(PAGESIZE);
        if (pageSize_value != null) {
            for (int i = pageSize_value.length - 1; i >= 0; i--) {
                String v = pageSize_value[i];
                if (StringUtils.isNoneBlank(v)) {
                    sbf.append("&").append(PAGESIZE).append("=").append(v);
                    break;// 从最后开始找，找到一个就返回了
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 获取分页HTML代码（不常用）
     * 只供临时测试使用，不建议在正式的系统中使用本方法输出分页条（因为“表现”与“数据”没有实现分离）。
     * 在正式的系统中应使用page.jsp做为模板、Page类做为数据，使用模板技术来输出自定义的分页导航条
     *
     * @return HTML代码
     */
    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        if (pageNo == first) {
            // 如果是首页,"上一页"不可点击
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>\n");
        } else {
            // 如果不是首页,"上一页"可点击
            sb.append("<li><a href=\"" + pageURL + prev + "\" >&#171; 上一页</a></li>\n");
        }

        int begin = pageNo - (length / 2);// 左边的起始页码，如当前是第50页，最左边的页码是46

        if (begin < first) {
            begin = first;// 防止出现负数
        }

        int end = begin + length - 1;// 右边的结束页码，如当前是第50页，最右边的页码是54

        if (end >= last) {
            end = last; // 防止超过总页数
            begin = end - length + 1;
            if (begin < first) {
                begin = first;
            }
        }

        // 在循环中生成 左边的页码。slider为1时，作用就是“首页”
        if (begin > first) {
            int i = 0;
            for (i = first; i < first + slider && i < begin; i++) {
                sb.append("<li><a href=\"" + pageURL + i + "\" >" + (i + 1 - first) + "</a></li>\n");
            }
            if (i < begin) {
                sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            }
        }

        // 在循环中生成 中间的页码，如当前是第50页，46，47，48，49，50，51，52，53，54，并且50页不可点击
        for (int i = begin; i <= end; i++) {
            if (i == pageNo) {
                sb.append("<li class=\"active\"><a href=\"javascript:\">" + (i + 1 - first) + "</a></li>\n");
            } else {
                sb.append("<li><a href=\"" + pageURL + i + "\" >" + (i + 1 - first) + "</a></li>\n");
            }
        }

        if (last - end > slider) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            end = last - slider;
        }

        // 在循环中生成 右边的页码。slider为1时，作用就是“尾页”
        for (int i = end + 1; i <= last; i++) {
            sb.append("<li><a href=\"" + pageURL + i + "\" >" + (i + 1 - first) + "</a></li>\n");
        }

        if (pageNo == last) {
            // 如果是尾页,"下一页"不可点击
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">下一页 &#187;</a></li>\n");
        } else {
            // 如果不是尾页,"下一页"可点击
            sb.append("<li><a href=\"" + pageURL + next + "\" >" + "下一页 &#187;</a></li>\n");
        }

        sb.append("<li class=\"disabled controls\"><a href=\"javascript:\">跳到 ");
        sb.append("<input type=\"text\" value=\"" + pageNo + "\" href=\"" + pageURL + "\" ");
        sb.append("onkeypress=\"var e=window.event||this;var c=e.keyCode||e.which;if(c==13)");
        sb.append("window.location.href=this.getAttribute('href')+(this.value)\" onclick=\"this.select();\"/> ");
        sb.append("共 " + count + " 条" + (message != null ? message : "") + "</a></li>\n");
        sb.insert(0, "<ul>\n").append("</ul>\n");
        sb.append("<div style=\"clear:both;\"></div>");
        return sb.toString();
    }

    /**
     * 获取总记录条数
     */
    public long getCount() {
        return count;
    }

    /**
     * 设置总记录条数
     *
     * @param count 总记录条数
     */
    public void setCount(long count) {
        this.count = count;
        initialize();//这里需要initialize(),执行完求count的SQL时会调用setCount()方法，所以需要initialize()
    }

    /**
     * 获取当前页码
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页码
     *
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 获取页面大小
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置页面大小
     *
     * @param pageSize -1表示不分页,是合法值
     */
    public void setPageSize(int pageSize) {
        int defValue=20;
        if (pageSize == 0) {
            try{
                this.pageSize = Global.getConfigInteger("page.pageSize", defValue);
            }catch (Throwable e){
                this.pageSize =defValue;
            }

        } else if (pageSize < -1) { //-1表示不分页,是合法值,所在排除掉-1
            try{
                this.pageSize = Global.getConfigInteger("page.pageSize", defValue);
            }catch (Throwable e){
                this.pageSize =defValue;
            }

        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * 首页索引
     */
    @JsonIgnore
    public int getFirst() {
        return first;
    }

    /**
     * 尾页索引
     */
    @JsonIgnore
    public int getLast() {
        return last;
    }

    /**
     * 获取页面总数
     *
     * @return getLast();
     */
    @JsonIgnore
    public int getTotalPage() {
        return getLast();
    }

    /**
     * 是否为第一页
     */
    @JsonIgnore
    public boolean isFirstPage() {
        return firstPage;
    }

    /**
     * 是否为最后一页
     */
    @JsonIgnore
    public boolean isLastPage() {
        return lastPage;
    }

    /**
     * 上一页索引值
     */
    @JsonIgnore
    public int getPrev() {
        if (isFirstPage()) {
            return pageNo;
        } else {
            return pageNo - 1;
        }
    }

    /**
     * 下一页索引值
     */
    @JsonIgnore
    public int getNext() {
        if (isLastPage()) {
            return pageNo;
        } else {
            return pageNo + 1;
        }
    }

    /**
     * 获取本页数据对象列表
     *
     * @return List<T>
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 设置本页数据对象列表
     *
     * @param list list
     */
    public Page<T> setList(List<T> list) {
        this.list = list;
        initialize();
        return this;
    }

    /**
     * 获取查询排序字符串
     * 可防止Order By部分的SQL注入
     */
    @JsonIgnore
    public String getOrderBy() {
        if (StringUtils.isBlank(orderBy)) {
            return null;
        }
        // SQL过滤，防止SQL注入
        String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
        Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(orderBy).find()) {
            return null;
        }
        return orderBy;
    }

    /**
     * 设置查询的排序条件
     * 标准查询有效，实例： updatedate desc, name asc
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 设置提示消息，显示在“共n条”之后
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 分页是否有效
     *
     * @return this.pageSize==-1
     */
    @JsonIgnore
    public boolean isDisabled() {
        return this.pageSize == -1;
    }

    /**
     * 是否进行总数统计
     *
     * @return this.count==-1
     */
    @JsonIgnore
    public boolean isNotCount() {
        return this.count == -1;
    }

    /**
     * 获取 Hibernate FirstResult，工作流模块要使用本方法
     * SQLHelper类要使用本方法(重要)
     */
    public int getFirstResult() {
        int firstResult = (getPageNo() - 1) * getPageSize();
        if (firstResult >= getCount()) {
            firstResult = 0;
        }
        return firstResult;
    }

    /**
     * 获取 Hibernate MaxResults，工作流模块要使用本方法
     * SQLHelper类要使用本方法(重要)
     */
    public int getMaxResults() {
        return getPageSize();
    }

    public String getPageURL() {
        return pageURL;
    }

    public int getLength() {
        return length;
    }

    public int getSlider() {
        return slider;
    }

    public String getMessage() {
        return message;
    }

    public boolean isBeyondPage() {
        return beyondPage;
    }

    public void setBeyondPage(boolean beyondPage) {
        this.beyondPage = beyondPage;
    }
}