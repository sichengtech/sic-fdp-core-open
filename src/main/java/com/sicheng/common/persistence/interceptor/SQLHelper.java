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
package com.sicheng.common.persistence.interceptor;

import com.sicheng.common.utils.Reflections;
import com.sicheng.common.utils.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL工具类
 *
 * @author zhaolei
 * @version 2013-8-28
 */
public class SQLHelper {

    /**
     * 对SQL参数(?)设值, 是为 countSql 服务的
     * 参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     *
     * @param ps              表示预编译的 SQL 语句的对象。
     * @param mappedStatement MappedStatement
     * @param boundSql        SQL
     * @param parameterObject 参数对象
     * @throws java.sql.SQLException 数据库异常
     */
    @SuppressWarnings("unchecked")
    public static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null :
                    configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("rawtypes")
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    /**
     * 执行一个求count总数的SQL，并返回一个值：总记录数（只能返回一个值）
     * 求count总数的SQL一共有3种。
     * 本方法已支持Mybatis二级缓存
     *
     * @param countSql        求countSql语句
     * @param connection      数据库连接
     * @param mappedStatement mapped
     * @param parameterObject 参数
     * @param boundSql        boundSql
     * @param log             日志工具
     * @param metaExecutor
     * @param rowBounds
     * @return 返回一个值：总记录数（只能返回一个值）
     * @throws SQLException
     */
    public static int execSQL(final String countSql, final Connection connection, final MappedStatement mappedStatement,
                              final Object parameterObject, final BoundSql boundSql, Log log, MetaObject metaExecutor,
                              RowBounds rowBounds) throws SQLException {
        //获得Mybatis的全局配置项cacheEnabled，是否开启了二级缓存
        //在Mybatis核心配置文件mybatis-config.xml中
        //<!-- 全局映射器启用缓存 -->
        //<setting name="cacheEnabled" value="true"/>  表示开启
        //<setting name=”cacheEnabled” value=”false” /> 表示关闭
        Configuration c = mappedStatement.getConfiguration();
        boolean isCacheEnabled = c.isCacheEnabled();

        CacheKey cacheKey = null;//缓存的key
        Cache cache = mappedStatement.getCache();//二级缓存的实现
        Connection conn = connection;//数据库连接
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Configuration configuration = mappedStatement.getConfiguration();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            BoundSql newBoundSql_count = new BoundSql(configuration, countSql, parameterMappings, parameterObject);

            // 解决mapper.xml文件使用bind指令或foreach指令新绑定的参数取不到问题  赵磊修改2017-10-25
            @SuppressWarnings("unchecked")
            Map<String, Object> addMap = (Map<String, Object>) Reflections.getFieldValue(boundSql, "additionalParameters");
            for (String name : addMap.keySet()) {
                newBoundSql_count.setAdditionalParameter(name, addMap.get(name));
            }

            //如果开启了缓存，则从缓存中查询“总记录数”
            if (cache != null && mappedStatement.isUseCache() && isCacheEnabled) {
                // 生成 countSql的缓存专用key
                SimpleExecutor ex = new SimpleExecutor(configuration, null);//SimpleExecutor有生成key的方法，直接使用就行了
                cacheKey = ex.createCacheKey(mappedStatement, parameterObject, rowBounds, newBoundSql_count);

                //从缓存中查询“总记录数”，countSql就是key
                Object value = cache.getObject(cacheKey);
                if (value != null) {
                    ////////////////////////////////////////////////////////////
                    //在缓存中找到了，就直接返回，不再执行countSql。起到了缓存的作用
                    ////////////////////////////////////////////////////////////
                    return (int) value;
                    //return (Integer) value;
                }
            }

            ////////////////////////////////////////////////////////////
            //在缓存中未找到，要执行数据库查询
            ////////////////////////////////////////////////////////////
            long t1 = System.currentTimeMillis();

            //打开数据库连接
            if (conn == null) {
                conn = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            }
            ps = conn.prepareStatement(countSql);

            //对SQL参数(?)设值
            SQLHelper.setParameters(ps, mappedStatement, newBoundSql_count, parameterObject);
            //System.out.println(newBoundSql_count.getSql());

            //执行查询
            rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            //如果开启了缓存，把“总记录数”写入缓存
            if (cache != null && mappedStatement.isUseCache() && isCacheEnabled) {
                TransactionalCacheManager tcm = (TransactionalCacheManager) metaExecutor.getValue("tcm");
                tcm.putObject(cache, cacheKey, count);
            }
            long t2 = System.currentTimeMillis();
            Log log2 = mappedStatement.getStatementLog();
            if (log2.isDebugEnabled()) {
                log2.debug("COUNT SQL: " + removeBreakingWhitespace(countSql) + "," + (t2 - t1) + "ms");
            }
            return count;//返回
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 去除sqlString的select子句。
     *
     * @param qlString
     * @return
     */
    public static String removeSelect(String qlString) {
        int beginPos = qlString.toLowerCase().indexOf("from");
        return qlString.substring(beginPos);
    }

    /**
     * 去除sql的orderBy子句。
     * Pattern.CASE_INSENSITIVE 表示启用不区分大小写的匹配
     * <p>
     * // 简单SQL示例
     * // 目标是：删除ORDER BY a.id desc部分
     * // String sql = "SELECT *  FROM table_name order by id desc";
     * <p>
     * // 复杂SQL示例1
     * // 目标：不应删除括号内的ORDER BY a.id desc
     * // "String sql = "SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
     * // "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ";
     * <p>
     * // 复杂SQL示例2
     * // 下面这个SQL是可以正常执行的SQL，它有两层并用括号分隔。每层都有ORDER BY，内层的ORDER BY子句不可删除，否则会导致取前6条数据的不一样。只想删除最外层的ORDER BY子句
     * // SELECT * FROM
     * // (SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 )t
     * // GROUP BY id ORDER BY id ASC
     *
     * @param sql
     * @return
     */
    public static String removeOrders(String sql) {
// 以下是第一代方案
//        //Pattern p = Pattern.compile("(order\\s+by[\\w|\\W|\\s|\\S]*)[^\\)]*", Pattern.CASE_INSENSITIVE);//这是原始的正则
//        Pattern p = Pattern.compile("(order\\s+by[^\\)]*)", Pattern.CASE_INSENSITIVE);//这是我后改进的正则，遇到)侧停止
//        Matcher m = p.matcher(sql);
//        StringBuffer sb = new StringBuffer();
//        while (m.find()) {
//            m.appendReplacement(sb, "");
//        }
//        m.appendTail(sb);
//        return sb.toString();

// 以下是第二代方案
//            int c = sql.toLowerCase().indexOf("order by ", b);//order by 子句只会出现最后,所以order后面的内容全都截掉（比如LIMIT子句，原始的分页SQL就不应带LIMIT）
//            if (c != -1) {
//                String s1 = sql.substring(0, c);
//                return s1;
//            }

// 以下是第三代方案 （不断改进中）
        int a = sql.indexOf("(");
        int b = sql.lastIndexOf(")");
        if (a != -1 && b != -1) {
            //发现了括号
            List<String> positionList = SQLHelper.parseSQL(sql);

            String key = "order by ";//关键字
            int s = 0;
            int rs = 0;//结果，外层的“order by”的起始下标
            while (true) {
                int c = sql.toLowerCase().indexOf(key, s);
                if (c == -1) {
                    break;
                } else {
                    //走到这里，说明发现了至少一个“order by”。
                    s = c + key.length();
                    //通过对比下标，证明此“order by”不是 括号内的子查询SQL语句中的“order by”。 目标是要找出最外层的“order by”
                    boolean isInSub = false;
                    for (String position : positionList) {
                        String[] positionArry = position.split(",");
                        if (Integer.valueOf(positionArry[0]) <= c && s <= Integer.valueOf(positionArry[1])) {
                            //是,证明此“order by”是 括号内的子查询SQL语句中的“order by”
                            isInSub = true;
                            break;
                        }
                    }
                    if (!isInSub) {
                        //找到了一个最外层“order by”，应该只会有一个
                        rs = c;//得到了结果，外层的“order by”的起始下标
                        break;//结束
                    }
                }
            }
            if (rs > 0) {
                //截掉最外层“order by”
                //order by 子句只会出现在SQL的最后,所以order后面的内容全都截掉（比如LIMIT子句，原始的分页SQL就不应带LIMIT）
                String s1 = sql.substring(0, rs);
                return s1;
            }
        } else {
            //没有括号，是只有一层的SQL
            int c = sql.toLowerCase().indexOf("order by ");//order by 子句只会出现最后,所以order后面的内容全都截掉（比如LIMIT子句）
            if (c != -1) {
                String s1 = sql.substring(0, c);
                return s1;
            }
        }
        return sql;
    }

    /**
     * 解析SQL，识别出括号内是子查询,就把这段文本的下标记录下来。
     *
     * @param sql 原始SQL
     * @return List<String> 子查询SQL文本的下标
     */
    public static List<String> parseSQL(String sql) {
        List<String> position = new ArrayList<>();
        if (StringUtils.isBlank(sql)) {
            return position;
        }
        char left = '(';//左半是括号
        char right = ')';//右半是括号

//        Stack来自于Vector，那么显然stack的底层实现是数组。
//        Stack的方法
//        1. java中Stack只有一个无参构造函数。
//        2. 属于stack自己的方法包括
//        push( num) //入栈
//        pop() //栈顶元素出栈
//        empty() //判定栈是否为空
//        peek() //获取栈顶元素
//        search(num) //判端元素num是否在栈中，如果在返回1，不在返回-1。
//        3.注意pop()和peek()的区别。pop()会弹出栈顶元素并返回栈顶的值，peek()只是获取栈顶的值，但是并不会把元素从栈顶弹出来。
        Stack<Integer> stack = new Stack(); //栈

        char[] arr = sql.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (left == arr[i]) {
                //发现左括号，压栈
                stack.push(i);
            }
            if (right == arr[i]) {
                //发现右括号，出栈
                int startIndex = stack.pop();//左括号的下标
                int endIndex = i;//右括号的下标
                String content = sql.substring(startIndex + 1, endIndex);//括号内的内容
                //判断括号内有没有select关键字
                boolean bl = content.trim().toLowerCase().startsWith("select ");
                if (bl) {
                    //发现括号内有select关键字，识别出括号内是子查询,就把这段文本的下标记录下来。
                    position.add(startIndex + "," + endIndex);
                }
            }
        }
        return position;
    }

    /**
     * 取得sql的orderBy子句。
     * Pattern.CASE_INSENSITIVE 表示启用不区分大小写的匹配
     * <p>
     * // 简单SQL示例
     * // 目标是：取得ORDER BY a.id desc部分
     * // String sql = "SELECT *  FROM table_name order by id desc";
     * <p>
     * // 复杂SQL示例1
     * // 目标：不应取得括号内的ORDER BY a.id desc
     * // "String sql = "SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
     * // "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ";
     * <p>
     * // 复杂SQL示例2
     * // 下面这个SQL是可以正常执行的SQL，它有两层并用括号分隔。每层都有ORDER BY，内层的ORDER BY子句不可取得，否则会导致取前6条数据的不一样。只想取得最外层的ORDER BY子句
     * // SELECT * FROM
     * // (SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 )t
     * // GROUP BY id ORDER BY id ASC
     *
     * @param sql
     * @return
     */
    public static String getOrders(String sql) {
// 以下是第一代方案
//        int a = sql.indexOf("(");
//        int b = sql.lastIndexOf(")");
//        if (a != -1 && b != -1) {
//            //发现了最外层的括号
//            int c = sql.toLowerCase().indexOf("order", b);//order by 子句只会出现最后,所以order后面的内容全都截掉
//            if (c != -1) {
//                String s2 = sql.substring(c);
//                return s2;
//            }
//        } else {
//            //没有括号，是只有一层的SQL
//            int c = sql.toLowerCase().indexOf("order");//order by 子句只会出现最后,所以order后面的内容全都截掉
//            if (c != -1) {
//                String s2 = sql.substring(c);
//                return s2;
//            }
//        }
//        return "";

// 以下是第二代方案
        int a = sql.indexOf("(");
        int b = sql.lastIndexOf(")");
        if (a != -1 && b != -1) {
            //发现了括号
            List<String> positionList = SQLHelper.parseSQL(sql);

            String key = "order by ";//关键字
            int s = 0;
            int rs = 0;//结果，外层的“order by”的起始下标
            while (true) {
                int c = sql.toLowerCase().indexOf(key, s);
                if (c == -1) {
                    break;
                } else {
                    //走到这里，说明发现了至少一个“order by”。
                    s = c + key.length();
                    //通过对比下标，证明此“order by”不是 括号内的子查询SQL语句中的“order by”。 目标是要找出最外层的“order by”
                    boolean isInSub = false;
                    for (String position : positionList) {
                        String[] positionArry = position.split(",");
                        if (Integer.valueOf(positionArry[0]) <= c && s <= Integer.valueOf(positionArry[1])) {
                            //是,证明此“order by”是 括号内的子查询SQL语句中的“order by”
                            isInSub = true;
                            break;
                        }
                    }
                    if (!isInSub) {
                        //找到了一个最外层“order by”，应该只会有一个
                        rs = c;//得到了结果，外层的“order by”的起始下标
                        break;//结束
                    }
                }
            }
            if (rs > 0) {
                //截掉最外层“order by”
                //order by 子句只会出现在SQL的最后,所以order后面的内容全都截掉（比如LIMIT子句，原始的分页SQL就不应带LIMIT）
                String s1 = sql.substring(rs);
                return s1;
            }
        } else {
            //没有括号，是只有一层的SQL
            int c = sql.toLowerCase().indexOf("order by ");//order by 子句只会出现最后,所以order后面的内容全都截掉（比如LIMIT子句）
            if (c != -1) {
                String s1 = sql.substring(c);
                return s1;
            }
        }
        return "";
    }

    /**
     * 美化sql
     * 去掉多余的空格、回车等符号，在输出日志时方便阅读
     *
     * @param original
     * @return
     */
    public static String removeBreakingWhitespace(String original) {
        StringTokenizer whitespaceStripper = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        while (whitespaceStripper.hasMoreTokens()) {
            builder.append(whitespaceStripper.nextToken());
            builder.append(" ");
        }
        return builder.toString();
    }

    /**
     * 从SQL中提出暗示语法，SQL中如果包含多个暗示语法就都提取出来
     * String sql="SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * 会取出：[FORCE_MASTER, ABCD]
     *
     * @param sql "SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * @return [FORCE_MASTER, ABCD]  暗示语法集合
     */
    public static List<String> hintList(String sql) {
        //SQL的暗示语法示例
        //1、强制在主库执行/*FORCE_MASTER*/，解决主从延迟问题。示例如下：SELECT * /*FORCE_MASTER*/  FROM table_name
        //2、关闭查询缓冲/* ENGINE_NO_CACHE */，例： select /* engine_no_cache */ count(*) from t1
        List<String> list = new ArrayList<>(0);

        // \s 匹配任何不可见字符，包括空格、制表符、换页符等等。等价于[ \f\n\r\t\v]。
        // \S 匹配任何可见字符。等价于[^ \f\n\r\t\v]。
        // ? 当该字符紧跟在任何一个其他限制符（*,+,?，{n}，{n,}，{n,m}）后面时，匹配模式是非贪婪的。非贪婪模式尽可能少地匹配所搜索的字符串，而默认的贪婪模式则尽可能多地匹配所搜索的字符串。例如，对于字符串“oooo”，“o+”将尽可能多地匹配“o”，得到结果[“oooo”]，而“o+?”将尽可能少地匹配“o”，得到结果 ['o', 'o', 'o', 'o']
        Pattern p = Pattern.compile("/\\*\\s*(.+?)\\s*\\*/");
        Matcher m = p.matcher(sql);
        while (m.find()) {
            int count = m.groupCount();//找到几个
//            System.out.println("找到了"+count+"个");
//            System.out.println(m.group(0));//取整体
//            System.out.println(m.group(1));//取第1个括号内的
            list.add(m.group(1));
        }
        return list;
    }

    /**
     * 从SQL中提出暗示语法，还要匹配前缀
     * String sql="SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * 会取出：FORCE_MASTER
     *
     * @param sql    "SELECT * \/* \n FORCE_MASTER *\/  FROM table_name  \/* ABCD*\/";
     * @param prefix force 表示只取以force开头的
     * @return FORCE_MASTER暗示语法 或 找不到时返回null
     */
    public static String hint4Prefix(String sql, String prefix) {
        if (StringUtils.isBlank(prefix) || StringUtils.isBlank(sql)) {
            return null;
        }
        List<String> hintList = SQLHelper.hintList(sql);
        for (String hint : hintList) {
            if (hint.toLowerCase().startsWith(prefix.toLowerCase())) {
                return hint;
            }
        }
        return null;
    }

    /**
     * 检查启用了“分页二次偏移量算法”的暗示语法
     * 格式:\/*paged_offset&pk=id,aid*\/
     * 格式说明:字母不区分大小写。
     * 格式说明:paged_offset是名称，表示使用“分页二次偏移量算法”来计算分页。
     * 格式说明:pk=id,aid是告诉框架主建列是id,aid，框架使用知主键列的名称。
     * 格式说明:&是两个参数之间的分隔符号
     * 格式说明:放置位置，暗示语法可放在SQL的任意位置
     * 示例：SELECT * \/*paged_offset&pk=id,aid*\/  FROM table_name;
     *
     * @param sql "SELECT * \/* paged_offset&pk=id,aid *\/  FROM table_name ";
     * @return paged_offset&pk=id,aid 暗示语法 或 找不到时返回null
     */
    public static String hint4LimitOffset(String sql) {
        return SQLHelper.hint4Prefix(sql, "paged_offset"); //paged_offset是关键字，不区分大小写
    }

    public static void main(String[] args) {

        String sql1 = "SELECT * FROM table_name order by id desc";

        String sql2 = "SELECT * FROM (SELECT a.id ,a.name ,b.title  FROM `cms_category` a " +
                "LEFT JOIN cms_article b ON a.id=b.`category_id` GROUP BY a.id HAVING a.id ORDER BY a.id desc)t ";

        String sql3 = "SELECT * FROM" +
                "(SELECT a.id  FROM `cms_category` a  GROUP BY a.id HAVING a.id ORDER BY a.id DESC LIMIT 6 ) t" +
                " GROUP BY id ORDER BY id ASC";

        System.out.println(SQLHelper.getOrders(sql1));
        System.out.println(SQLHelper.getOrders(sql2));
        System.out.println(SQLHelper.getOrders(sql3));

    }

}