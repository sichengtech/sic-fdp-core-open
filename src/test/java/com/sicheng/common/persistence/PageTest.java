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

import com.sicheng.common.persistence.Page;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>标题: page分页的单元测试  </p>
 * <p>描述: 对page进行基础的测试  </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @version 2019-12-01 11:22
 *
 * <p>重要修改历史记录1: xxxx  。修改人：xx</p>
 * <p>重要修改历史记录2: xxxx  。修改人：xx</p>
 */
public class PageTest {

    /**
     * 方法说明：正常参数测试
     *
     * @param
     * @return void
     * @author zhalei
     * @version 2019-12-01 15:44
     */
    @Test
    public void test1() {
        int pageNo = 1;
        int pageSize = 20;
        long count = 100;
        Page page = new Page();
        page.setCount(count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 1);
        Assert.assertEquals(page.getPageSize(), 20);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 5);
        Assert.assertEquals(page.getTotalPage(), 5);
        Assert.assertEquals(page.isFirstPage(), true);
        Assert.assertEquals(page.isLastPage(), false);
        Assert.assertEquals(page.getPrev(), 1);
        Assert.assertEquals(page.getNext(), 2);
        page.getHtml();
        page.getPageURL();
        page.getOrderBy();
        page.getMessage();
        page.getList();
    }

    /**
     * 方法说明：正常参数测试
     * 模拟业务过程，较晚的执行page.setCount(count)的效果
     *
     * @param
     * @return void
     * @author zhalei
     * @version 2019-12-01 15:44
     */
    @Test
    public void test2() {
        int pageNo = 2;
        int pageSize = 20;
        long count = 100;
        Page page = new Page(pageNo, pageSize);
        Assert.assertEquals(page.getCount(), 0);
        Assert.assertEquals(page.getPageNo(), 2);
        Assert.assertEquals(page.getPageSize(), 20);
        Assert.assertEquals(page.getFirst(), 0);
        Assert.assertEquals(page.getLast(), 0);

        page.setCount(count);  //就是测试较晚setCount(count)的效果
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 5);
        Assert.assertEquals(page.getTotalPage(), 5);
        Assert.assertEquals(page.isFirstPage(), false);
        Assert.assertEquals(page.isLastPage(), false);
        Assert.assertEquals(page.getPrev(), 1);
        Assert.assertEquals(page.getNext(), 3);
        page.getHtml();
        page.getPageURL();
        page.getOrderBy();
        page.getMessage();
        page.getList();
    }

    /**
     * 方法说明：正常参数测试
     *
     * @param
     * @return void
     * @author zhalei
     * @version 2019-12-01 15:44
     */
    @Test
    public void test3() {
        int pageNo = 1;
        int pageSize = 20;
        long count = 100;
        Page page = new Page(pageNo, pageSize, count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 1);
        Assert.assertEquals(page.getPageSize(), 20);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 5);
        Assert.assertEquals(page.getTotalPage(), 5);
        Assert.assertEquals(page.isFirstPage(), true);
        Assert.assertEquals(page.isLastPage(), false);
        Assert.assertEquals(page.getPrev(), 1);
        Assert.assertEquals(page.getNext(), 2);
        page.getHtml();
        page.getPageURL();
        page.getOrderBy();
        page.getMessage();
        page.getList();
    }


    /**
     * 方法说明：非正常参数测试
     *
     * @param
     * @return void
     * @author zhalei
     * @version 2019-12-01 15:44
     */
    @Test
    public void test4() {
        int pageNo = -1;
        int pageSize = -5;
        long count = 100;
        Page page = new Page(pageNo, pageSize, count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 1);
        Assert.assertEquals(page.getPageSize(), 20);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 5);
        Assert.assertEquals(page.getTotalPage(), 5);
        Assert.assertEquals(page.isFirstPage(), true);
        Assert.assertEquals(page.isLastPage(), false);
        Assert.assertEquals(page.isBeyondPage(), false);
    }

    /**
     * 方法说明：非正常参数测试
     *
     * @param
     * @return void
     * @author zhalei
     * @version 2019-12-01 15:44
     */
    @Test
    public void test5() {
        int pageNo = 74;
        int pageSize = -5;
        long count = 100;
        Page page = new Page(pageNo, pageSize, count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 5);
        Assert.assertEquals(page.getPageSize(), 20);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 5);
        Assert.assertEquals(page.getTotalPage(), 5);
        Assert.assertEquals(page.isFirstPage(), false);
        Assert.assertEquals(page.isLastPage(), true);
        Assert.assertEquals(page.isBeyondPage(), true);
    }


    @Test
    public void test6() {
        String pageNo = "4";
        int pageSize = 15;
        long count = 100;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        request.addParameter(Page.PAGENO, pageNo);
        HttpServletResponse response = new MockHttpServletResponse();
        Page page = new Page(request, response, pageSize);
        page.setCount(count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 4);
        Assert.assertEquals(page.getPageSize(), 15);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 7);
        Assert.assertEquals(page.getTotalPage(), 7);
        Assert.assertEquals(page.isFirstPage(), false);
        Assert.assertEquals(page.isLastPage(), false);
        Assert.assertEquals(page.isBeyondPage(), false);
    }

    @Test
    public void test7() {
        String pageNo = "7";
        int pageSize = 15;
        long count = 100;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        request.addParameter(Page.PAGENO, pageNo);
        HttpServletResponse response = new MockHttpServletResponse();
        Page page = new Page(request, response, pageSize);
        page.setCount(count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 7);
        Assert.assertEquals(page.getPageSize(), 15);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 7);
        Assert.assertEquals(page.getTotalPage(), 7);
        Assert.assertEquals(page.isFirstPage(), false);
        Assert.assertEquals(page.isLastPage(), true);
        Assert.assertEquals(page.isBeyondPage(), false);
    }

    @Test
    public void test8() {
        String pageNo = "100";
        int pageSize = 15;
        long count = 100;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        request.addParameter(Page.PAGENO, pageNo);
        HttpServletResponse response = new MockHttpServletResponse();
        Page page = new Page(request, response, pageSize);
        page.setCount(count);
        Assert.assertEquals(page.getCount(), 100);
        Assert.assertEquals(page.getPageNo(), 7);
        Assert.assertEquals(page.getPageSize(), 15);
        Assert.assertEquals(page.getFirst(), 1);
        Assert.assertEquals(page.getLast(), 7);
        Assert.assertEquals(page.getTotalPage(), 7);
        Assert.assertEquals(page.isFirstPage(), false);
        Assert.assertEquals(page.isLastPage(), true);
        Assert.assertEquals(page.isBeyondPage(), true);
    }
}