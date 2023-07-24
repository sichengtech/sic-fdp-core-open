/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.dialect;

import com.sicheng.common.config.Global;
import com.sicheng.common.persistence.interceptor.SQLHelper;

/**
 * 各个数据库的分页SQL方言
 * 类似hibernate的Dialect,但只精简出分页部分
 */
public interface Dialect {

    /**
     * 数据库本身是否支持分页当前的分页查询方式
     * 如果数据库不支持的话，则不进行数据库分页
     *
     * @return true：支持当前的分页查询方式
     */
    boolean supportsLimit();

    /**
     * 将一条普通的业务sql转换为支持分页SQL
     * 这是最常规的分页SQL
     * 单表、多对一、一对一，都使用本分页SQL，使用频率高达90%
     *
     * <pre>
     * 如mysql
     * dialect.pagedSql("select * from user", 12, ":offset",0,":limit") 将返回
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql    业务sql
     * @param offset 开始条数
     * @param limit  每页显示多少纪录条数
     * @return 支持分页查询的sql
     */
    String pagedSql(String sql, int offset, int limit);


    /**
     * 将一条普通的业务sql转换为 求count的SQL
     * 这是最常规的求count的SQL，与pagedSql()返回的SQL配合使用。
     * <p>
     * 为什么default修饰的？因为本方法是后期加的。由于情况并不多样，不需要每个子类做出不同的实现，就写在了接口中。
     *
     * @param sql 业务sql
     * @return 求count的SQL
     */
    default String countSql(final String sql) {
        String dbType$ = Global.getConfig("jdbc.type");
        final String countSql;
        if ("oracle".equals(dbType$)) {
            countSql = "select count(1) from (" + sql + ") tmp_count";
        } else {
            //求总数时使用removeOrders()去掉 order by排序子句，一是求总数时不需要排序，二是不花费排序的性能。
            //mysql5.6的子查询性能已得到极大的提升，建议使用子查询。以下语句改为子查询
            //countSql = "select count(1) " + removeSelect(removeOrders(sql));
            countSql = "select count(1) from (" + SQLHelper.removeOrders(sql) + ") tmp_count";
        }
        return countSql;
    }

    /**
     * 将一条普通的业务sql转换为支持“二次偏移量算法”分页SQL
     * <p>
     * 一对多join时，使用本分页SQL，使用频率低达10%
     * 一对多的多表join的SQL,执行后导致左表记录数变多、分页不准、合并后不足一页的问题
     * 将通过用本方法计算出来的二次偏移量来解决。
     * <p>
     * “分页二次偏移量算法”暗示语法
     * 只在要SQL字符串任意位置包含以下语法，就会开启“分页二次偏移量算法”
     * 格式:\/*paged_offset&pk=id,aid*\/
     * 格式说明:字母不区分大小写。
     * 格式说明:paged_offset是名称，表示使用“分页二次偏移量算法”来计算分页。
     * 格式说明:pk=id,aid是告诉框架主建列是id,aid，框架使用知主键列的名称。
     * 格式说明:&是两个参数之间的分隔符号
     * 格式说明:放置位置，暗示语法可放在SQL的任意位置
     * 示例：SELECT * \/*paged_offset&pk=id,aid*\/  FROM table_name;
     *
     * @param sql    SQL语句
     * @param hint   暗示语法 \/*paged_offset#pk=a.id*\/
     * @param offset 开始条数（原始值）
     * @param limit  每页显示多少纪录条数（原始值）
     * @return 支持“二次偏移量算法”分页SQL
     */
//    String pagedSql4joinOffset(String sql, String hint, int offset, int limit);

    /**
     * 将一条普通的业务sql转换为支持“二次偏移量算法”的求count的SQL(第一类)
     * 这是特殊的求count的SQL，与pagedSql4joinOffset()返回的SQL配合使用。
     * <p>
     * 为什么default修饰的？因为本方法是后期加的。由于情况并不多样，不需要每个子类做出不同的实现，就写在了接口中。
     *
     * @param sql  SQL语句
     * @param hint 暗示语法 \/*paged_offset#pk=a.id*\/
     * @return 支持“二次偏移量算法”的求count的SQL
     */
    default String countSql4joinOffset(String sql, String hint) {
        ////////////////////////////////////////////////////////
        //“二次偏移量算法”一共由3条(2类)SQL计算出偏移量,这是第一类
        ////////////////////////////////////////////////////////
//        #求合并后的总记录数
//        SELECT COUNT(t.栏目ID) AS '总记录数' FROM (
//                SELECT a.id "栏目ID",COUNT(a.id) AS "合并的数量" FROM `cms_category` a LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id
//        ) t

        //取出主键从“paged_offset#pk=a.id”中取出a.id主键列名, a是表的别名
        String[] arr = hint.split("#");//“paged_offset#pk=a.id”中取出pk=a.id
        String pk = arr[1].split("=")[1];//pk=a.id中取出a.id

        // 原始SQL去掉 select 子句，去掉Order by子句.
        // 求总数时使用removeOrders()去掉 order by排序子句，一是求总数时不需要排序，二是不花费排序的性能。
        String sqlSub = SQLHelper.removeSelect(SQLHelper.removeOrders(sql));

        //组装
        StringBuilder sbl = new StringBuilder();
        sbl.append("SELECT COUNT(idd) AS ccount FROM ( ");//ccount是别名
        sbl.append("SELECT ").append(pk).append(" AS idd ").append(sqlSub);
        sbl.append(" GROUP BY ").append(pk);//按ID分组是核心逻辑
        sbl.append(") t");
        return SQLHelper.removeBreakingWhitespace(sbl.toString());//美化sql
    }

    /**
     * 将一条普通的业务sql转换为支持“二次偏移量算法”的求count的SQL(第二类)
     * 这是特殊的求count的SQL，与pagedSql4joinOffset()返回的SQL配合使用。
     * <p>
     * 为什么default修饰的？因为本方法是后期加的。由于情况并不多样，不需要每个子类做出不同的实现，就写在了接口中。
     *
     * @param sql    SQL语句
     * @param hint   暗示语法 \/*paged_offset#pk=a.id*\/
     * @param offset 开始条数（原始值）
     * @param limit  每页显示多少纪录条数（原始值）
     * @return 支持“二次偏移量算法”的求count的SQL
     */
    default String countSql4joinOffset(String sql, String hint, int offset, int limit) {
        ////////////////////////////////////////////////////////
        //“二次偏移量算法”一共由3条(2类)SQL计算出偏移量,这是第二类
        ////////////////////////////////////////////////////////
//        #求未合并的2次偏移量-start
//        SELECT SUM(t.合并的数量) AS '2次偏移量-start' FROM (
//                SELECT a.id ,COUNT(a.id) AS "合并的数量" FROM `cms_category` a LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id ORDER BY a.id LIMIT 0,10
//        ) t
//
//        #求未合并的2次偏移量-end
//        SELECT SUM(t.合并的数量) AS '2次偏移量-ent' FROM (
//                SELECT a.id ,COUNT(a.id) AS "合并的数量" FROM `cms_category` a LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id ORDER BY a.id LIMIT 10,3
//        ) t

        //取出主键从“paged_offset#pk=a.id”中取出a.id主键列名, a是表的别名
        String[] arr = hint.split("#");//“paged_offset#pk=a.id”中取出pk=a.id
        String pk = arr[1].split("=")[1];//pk=a.id中取出a.id

        //取得sql中的原始的order By子句
        String orderBy=SQLHelper.getOrders(sql);

        // 原始SQL去掉 select 子句，去掉Order by子句
        //  order by排序子句 必须放在 group by 子句的后面，SQL语法才是正确的
        String sqlSub = SQLHelper.removeSelect(SQLHelper.removeOrders(sql));

        //组装
        StringBuilder sbl = new StringBuilder();
        sbl.append("SELECT SUM(size) AS ccount FROM ( ");//ccount是别名
        sbl.append("SELECT COUNT(").append(pk).append(") as size ").append(sqlSub);
        sbl.append(" GROUP BY ").append(pk);//按ID分组是核心逻辑
        sbl.append(" ");
        sbl.append(orderBy); //拼上原始的order by子句，它必需要放在GROUP BY子句的后面，才是正确的语法。
        sbl.append(" LIMIT ").append(offset).append(",").append(limit);
        sbl.append(") t");
        return SQLHelper.removeBreakingWhitespace(sbl.toString());//美化sql
    }
}
