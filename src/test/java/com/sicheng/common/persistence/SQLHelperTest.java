package com.sicheng.common.persistence;

import com.sicheng.common.persistence.interceptor.SQLHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * <p>标题: SQLHelper类的单元测试  </p>
 * <p>描述: xxxx  </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @version 2022-06-01 23:06
 *
 * <p>重要修改历史记录1: xxxx  。修改人：xx</p>
 * <p>重要修改历史记录2: xxxx  。修改人：xx</p>
 */
public class SQLHelperTest {

    /**
     * 测试：去除sql的orderBy子句。
     * 简单SQL示例
     * 目标是：删除ORDER BY a.id desc部分
     */
    @Test
    public void test_removeOrders_1() {
        String sql = "SELECT * FROM table_name order by id desc";
        String sql2 = SQLHelper.removeOrders(sql);
        Assert.assertEquals("SELECT * FROM table_name ", sql2);
    }

    /**
     * 测试：去除sql的orderBy子句。
     * 复杂SQL示例1
     * 目标：不应删除括号内的ORDER BY a.id desc
     */
    @Test
    public void test_removeOrders_2() {
        String sql = "SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
                "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ";
        String sql2 = SQLHelper.removeOrders(sql);
        System.out.println(sql2);
        Assert.assertEquals("SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
                "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ", sql2);
    }

    /**
     * 测试：去除sql的orderBy子句。
     * 复杂SQL示例2
     * <p>
     * 下面这个SQL是可以正常执行的SQL，它有两层并用括号分隔。每层都有ORDER BY，内层的ORDER BY子句不可删除，否则会导致取前6条数据的不一样。只想删除最外层的ORDER BY子句
     */
    @Test
    public void test_removeOrders_3() {
        String sql = "SELECT * FROM" +
                "(SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 ) t" +
                " GROUP BY id ORDER BY id ASC";
        String sql2 = SQLHelper.removeOrders(sql);
        String rs = "SELECT * FROM(SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 ) t GROUP BY id ";
        Assert.assertEquals(rs, sql2);
    }

    /**
     * 测试：去除sql的orderBy子句。
     * 复杂SQL示例3
     * 本SQL的特点是表名叫trade_order,列名叫ORDERNUM，其中包含order关键字
     * 目标是：删除ORDER BY payMoney DESC部分
     */
    @Test
    public void test_removeOrders_4() {
        String sql = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n" +
                "ORDER BY payMoney DESC";
        String sql2 = SQLHelper.removeOrders(sql);
        String rs = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n";
        System.out.println(sql2);
        Assert.assertEquals(rs, sql2);
    }

    /**
     * 测试：去除sql的orderBy子句。
     * 复杂SQL示例3
     * 本SQL的特点 GROUP BY SUM(tor.store_id)  ,在GROUP BY之后出现了括号。
     * 目标是：要识别出SUM(tor.store_id) 不是子查询 ，并删除ORDER BY SUM(tor.store_id) DESC部分
     */
    @Test
    public void test_removeOrders_5() {
        String sql = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n" +
                "ORDER BY SUM(tor.store_id) DESC";
        String sql2 = SQLHelper.removeOrders(sql);
        String rs = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n";
        System.out.println(sql2);
        Assert.assertEquals(rs, sql2);
    }

    /**
     * 测试：解析SQL，识别出括号内是子查询,就把这段文本的下标记录下来。
     */
    @Test
    public void test_parseSQL_1() {
        String sql = "SELECT * FROM" +
                "(SELECT a.id FROM `cms_category` a GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 ) t" +
                " GROUP BY id ORDER BY Max(id) ASC";
        List<String> list = SQLHelper.parseSQL(sql);
        System.out.println(list);
        Assert.assertEquals(list.toString(), "[13,101]");

    }

    /**
     * 测试：取得sql的orderBy子句。
     * 简单SQL示例
     * 目标是：取得ORDER BY a.id desc部分
     */
    @Test
    public void test_getOrders_1() {
        String sql = "SELECT * FROM table_name order by id desc";
        String rs = SQLHelper.getOrders(sql);
        Assert.assertEquals("order by id desc", rs);
    }

    /**
     * 测试：取得sql的orderBy子句。
     * 复杂SQL示例1
     * 目标：不应取得括号内的ORDER BY a.id desc, 目标是只取得到空
     */
    @Test
    public void test_getOrders_2() {
        String sql = "SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
                "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ";
        String rs = SQLHelper.getOrders(sql);
        System.out.println(rs);
        Assert.assertEquals("", rs);
    }

    /**
     * 测试：取得sql的orderBy子句。
     * 复杂SQL示例2
     * <p>
     * 下面这个SQL是可以正常执行的SQL，它有两层并用括号分隔。每层都有ORDER BY，内层的ORDER BY子句不可取得。只想取得最外层的ORDER BY子句
     */
    @Test
    public void test_getOrders_3() {
        String sql = "SELECT * FROM" +
                "(SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 ) t" +
                " GROUP BY id ORDER BY id ASC";
        String rs = SQLHelper.getOrders(sql);
        Assert.assertEquals(rs, "ORDER BY id ASC");
    }

    /**
     * 测试：取得sql的orderBy子句。
     * 复杂SQL示例3
     * 本SQL的特点是表名叫trade_order,列名叫ORDERNUM，其中包含order关键字
     * 目标是：取得ORDER BY payMoney DESC部分
     */
    @Test
    public void test_getOrders_4() {
        String sql = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n" +
                "ORDER BY payMoney DESC";
        String rs = SQLHelper.getOrders(sql);
        Assert.assertEquals(rs, "ORDER BY payMoney DESC");
    }

    /**
     * 测试：取得sql的orderBy子句。
     * 复杂SQL示例3
     * 本SQL的特点 GROUP BY SUM(tor.store_id)  ,在GROUP BY之后出现了括号。
     * 目标是：要识别出SUM(tor.store_id) 不是子查询 ，并取得ORDER BY SUM(tor.store_id) DESC部分
     */
    @Test
    public void test_getOrders_5() {
        String sql = "SELECT\n" +
                "tor.store_id AS STOREID,\n" +
                "MAX(tor.b_name) AS STORENAME,\n" +
                "SUM(tor.amount_paid) AS PAYMONEY,\n" +
                "COUNT(tor.order_id) AS ORDERNUM,\n" +
                "SUM(toi.quantity) AS PRODUCTNUM\n" +
                "FROM trade_order tor LEFT JOIN trade_order_item toi ON tor.order_id=toi.order_id\n" +
                "WHERE tor.order_status<>10  AND tor.order_status<>60 AND tor.is_return_status='0' \n" +
                "GROUP BY tor.store_id\n" +
                "ORDER BY SUM(tor.store_id) DESC";
        String rs = SQLHelper.getOrders(sql);
        System.out.println(rs);
        Assert.assertEquals(rs, "ORDER BY SUM(tor.store_id) DESC");
    }

    /**
     * 从SQL中提出暗示语法，SQL中如果包含多个暗示语法就都提取出来
     * String sql="SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * 会取出：[FORCE_MASTER, ABCD]
     *
     * @param sql "SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * @return [FORCE_MASTER, ABCD]  暗示语法集合
     */
    @Test
    public void test_hintList_1() {
        String sql="SELECT * /* \n FORCE_MASTER */  FROM table_name  /* ABCD*/";
        List<String> list=SQLHelper.hintList(sql);
        System.out.println(list);
        Assert.assertEquals(list.toString(), "[FORCE_MASTER, ABCD]");
    }
}
