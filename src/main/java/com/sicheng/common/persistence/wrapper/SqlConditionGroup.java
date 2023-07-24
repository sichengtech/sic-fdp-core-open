/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.wrapper;

import com.sicheng.common.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>标题: SqlConditionGroup SQL条件组</p>
 * <p>描述: 存放where条件、having条件、group by条件、order by条件</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年1月31日 下午9:06:19
 */
public class SqlConditionGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 占位符
     * Wrapper是自己独有的占位符, %s不是正则，是格式化符号。
     * 示例：String.format({%s}, 10)会输出{10}
     */
    public static final String PLACE_HOLDER = "{%s}";
    /**
     * 匹配占位符的正则表达式
     */
    public static final String PLACE_HOLDER_REG = "\\{\\d+\\}";

    List<Object> whereList = new ArrayList<Object>();
    List<Object> havingList = new ArrayList<Object>();
    List<Object> groupByList = new ArrayList<Object>();
    List<Object> orderByList = new ArrayList<Object>();
    List<Object> lastList = new ArrayList<Object>();
    List<Object> andOrList = new ArrayList<Object>();
    String firstRelationOperator = null;//第一个关系运算符 and or

    /**
     * @Fields valueList : 存放占位符值（where条件的值）
     */
    Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

    /**
     * @Fields index :    占位符值（where条件的值）计数器
     */
    int index = 0;

    /**
     * @Title:构造方法
     */
    public SqlConditionGroup() {
        andOrList.add(SqlMake.AND);
        andOrList.add(SqlMake.OR);
        andOrList.add(SqlMake.AND_NEW);
        andOrList.add(SqlMake.OR_NEW);
    }

    /**
     * 拼接SQL语句
     * 拼接符合mybatis的规范的SQL
     * 示例：WHERE (p_id=#{wrapper.valueMap[param_0]} AND name=#{wrapper.valueMap[param_1]})
     *
     * @return
     */
    public String getOutputSqlAll() {
        index = 0;//清0还原
        StringBuilder builder = new StringBuilder(512);
        return buildSQL(builder);
    }

    public String getOutputSqlWhere() {
        index = 0;//清0还原
        StringBuilder builder = new StringBuilder(256);
        return buildSQL1(builder);
    }

    public String getOutputSqlOrderBy() {
        StringBuilder builder = new StringBuilder(32);
        return buildSQL2(builder);
    }

    /**
     * 获取值
     * SQL语句中?占位符值
     *
     * @return the valueMap
     */
    public Map<String, Object> getValueMap() {
        return valueMap;
    }


    /**
     * 按标准顺序连接并构建SQL,全包含
     *
     * @param builder 连接器
     * @return
     */
    private String buildSQL(StringBuilder builder) {
        buildSQL1(builder);
        buildSQL2(builder);
        return builder.toString();
    }

    /**
     * 按标准顺序连接并构建SQL,只包含where groupby having
     */
    private String buildSQL1(StringBuilder builder) {
        String fro = null;
        if (getFirstRelationOperator() != null) {
            fro = getFirstRelationOperator().trim();
        } else {
            fro = "AND";
        }
        sqlClause(builder, fro, whereList, "(", ")", SqlMake.AND);
        sqlClause(builder, "GROUP BY", groupByList, "", "", ", ");
        sqlClause(builder, "HAVING", havingList, "(", ")", SqlMake.AND);
        return builder.toString();
    }

    /**
     * 按标准顺序连接并构建SQL,只包含order by
     */
    private String buildSQL2(StringBuilder builder) {
        sqlClause(builder, "ORDER BY", orderByList, "", "", ", ");
        return builder.toString();
    }

    /**
     * 构建SQL的条件
     *
     * @param builder     连接器
     * @param keyword     TSQL中的关键字
     * @param parts       SQL条件语句集合
     * @param open        起始符号
     * @param close       结束符号
     * @param conjunction 连接条件
     */
    private void sqlClause(StringBuilder builder, String keyword, List<Object> parts, String open, String close, String conjunction) {
        parts = clearNull(parts);
        if (!parts.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n");
            }

            builder.append(keyword);
            builder.append(" ");
            builder.append(open);
            String last = "__";
            for (int i = 0, n = parts.size(); i < n; i++) {
                Object obj = parts.get(i);

                //检查并在两个条件之间补充AND关键字
                if (obj instanceof String) {
                    String part = (String) obj;
                    if (i > 0) {
                        if (andOrList.contains(part) || andOrList.contains(last)) {
                            builder.append(part);
                            last = part;
                            continue;
                        } else {
                            builder.append(conjunction);
                        }
                    }
                    builder.append(part);
                }
                //核心代码，拼接sql并使用占位符，再把参数放入map
                if (obj instanceof SqlConditionElement) {
                    SqlConditionElement element = (SqlConditionElement) obj;
                    sqlArgsFill(element, valueMap, builder);
                }
            }
            builder.append(close);
        }
    }

    /**
     * <p>SQL 参数填充</p>
     * 支持两种方案
     * 方案一：未用{0}{1}占位符方案
     * 方案二：使用{0}{1}占位符方案
     *
     * @param element  表示一个条件，有key有value
     * @param valueMap 把value填充到这里
     * @param builder  把key填充到这里,value的位置使用mybatis的#{}占位符占用
     * @return
     */
    private void sqlArgsFill(SqlConditionElement element, Map<String, Object> valueMap, StringBuilder builder) {
        String content = element.getCondition();
        Object[] args = element.getValues();

        if (StringUtils.isBlank(content)) {
            return;
        }

        if (hasPlaceHolder(content)) {
            //content包含有{0}{1}占位符，例如：.orNew("lower(name) like lower({0})", "%HuaWei%")
            if (args != null) {
                int len = args.length;
                if (len >= 1) {
                    for (int k = 0; k < len; k++) {
                        Object value = args[k];
                        List<Object> arrayList = arr2List(value);//数组转List，若不能转则返回null
                        boolean isList = (arrayList == null) ? false : true;//null说明value不是List类型

                        if (isList) {
                            //参数是List,List中有多个参数， in语句
                            if (arrayList.size() > 0) {
                                StringBuilder bl = new StringBuilder(64);
                                bl.append("(");
                                for (int j = 0; j < arrayList.size(); j++) {
                                    String k1 = getKey();
                                    bl.append(getKey2(k1));
                                    if (j != (arrayList.size() - 1)) {
                                        bl.append(",");
                                    }
                                    valueMap.put(k1, arrayList.get(j));
                                }
                                bl.append(")");
                                content = content.replace(String.format(PLACE_HOLDER, k), bl.toString());
                            } else {
                                //arrayList的长度为0，这个的SQL是无法运行的，需要特殊处理，在保证sql原意的同时保证能运行
                                content = "1=2";
                            }
                        } else {
                            //args[i]这一个参数是一个值，args[i]中不是List
                            String k1 = getKey();
                            content = content.replace(String.format(PLACE_HOLDER, k), getKey2(k1));
                            valueMap.put(k1, args[k]);
                        }
                    }
                }
            }
            builder.append(content);
        } else {
            //content不包含{0}{1}占位符，例如：.and("name like", "%手机")
            if (args == null || args.length == 0) {//无参数 is null
                builder.append(content);
            } else if (args.length == 1) {//一个参数 = <> <  > 语句
                Object value = args[0];

                List<Object> arrayList = arr2List(value);//数组转List，若不能转则返回null
                boolean isList = (arrayList == null) ? false : true;//null说明value不是List类型

                if (isList) {
                    //参数是List,List中有多个参数， 当是in语句时会遇到这种情况
                    if (arrayList.size() > 0) {
                        builder.append(content);
                        List<Object> list = arrayList;
                        builder.append("(");
                        for (int j = 0; j < list.size(); j++) {
                            String k1 = getKey();
                            builder.append(getKey2(k1));
                            if (j != (list.size() - 1)) {
                                builder.append(",");
                            }
                            valueMap.put(k1, list.get(j));
                        }
                        builder.append(")");
                    } else {
                        //arrayList的长度为0，这个的SQL是无法运行的，需要特殊处理，在保证sql原意的同时保证能运行
                        builder.append("1=2");
                    }
                } else {
                    //参数是一个值
                    String k1 = getKey();
                    builder.append(content + " " + getKey2(k1));
                    valueMap.put(k1, args[0]);
                }
            } else if (args.length == 2) {//两个参数 between
                String k1 = getKey();
                String k2 = getKey();
                builder.append(content + " " + getKey2(k1) + " AND " + getKey2(k2));
                valueMap.put(k1, args[0]);
                valueMap.put(k2, args[1]);
            } else {
                throw new WrapperException(content + "参数数量必须是0、1、2个,实际是" + args.length + "个");
            }
        }
    }

    /**
     * 数组转List
     * 判断 Object value的实际类型，
     * 若是数组类型或Collection类型，把数组转List，把Collection转List,并返回List<Object>
     * 若不是以上两种类型，返回null
     *
     * @param value
     * @return
     */
    private List<Object> arr2List(Object value) {
        List<Object> arrayList = null;
        if (value instanceof Collection) {
            arrayList = new ArrayList<Object>((Collection<?>) value);//转List
        } else if (value != null && value.getClass().isArray()) {
            int length = Array.getLength(value);
            arrayList = new ArrayList<Object>(length);
            for (int i = 0; i < length; i++) {
                Object arr_value = Array.get(value, i);
                arrayList.add(arr_value);// 数组转List
            }
        }
        return arrayList;
    }

    /**
     * 判断字符串中是否包含{0}{1}占位符
     *
     * @param content 字符串
     * @return
     */
    private boolean hasPlaceHolder(String content) {
        //Pattern.CASE_INSENSITIVE 让表达式忽略大小写进行匹配
        Pattern sqlPattern = Pattern.compile(PLACE_HOLDER_REG, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(content).find()) {
            return true;
        }
        return false;
    }

    /**
     * 清除LIST中的NULL和空字符串
     *
     * @param parts 原LIST列表
     * @return
     */
    private List<Object> clearNull(List<Object> parts) {
        List<Object> temps = new ArrayList<Object>(parts.size());
        for (Object obj : parts) {
            if (obj instanceof String) {
                String part = (String) obj;
                if (StringUtils.isBlank(part)) {
                    continue;
                }
            }
            if (obj instanceof SqlConditionElement) {
                SqlConditionElement element = (SqlConditionElement) obj;
                String condition = element.getCondition();
                if (StringUtils.isBlank(condition)) {
                    continue;
                }
            }
            temps.add(obj);
        }
        return temps;
    }


    /**
     * 生成key,作为map的key
     * 示例：param_0
     *
     * @return
     */

    private String getKey() {
        //return "p"+(index++);
        final StringBuilder buf = new StringBuilder(8);
        buf.append("p");
        buf.append(index++);
        return buf.toString();

    }

    /**
     * 生成key,作为mysql xml映射文件中的key
     * 示例：#{wrapper.valueMap['param_0']}
     *
     * @param key1 示例：param_0
     * @return
     */
    private String getKey2(String key1) {
        //return "#{w.v["+key1+"]}";
        final StringBuilder buf = new StringBuilder(32);
        buf.append("#{w.v[");
        buf.append(key1);
        buf.append("]}");
        return buf.toString();
    }

    /**
     * @return the firstRelationOperator
     */
    public String getFirstRelationOperator() {
        return firstRelationOperator;
    }

    /**
     * 只存储 第一个关系运算符 and or
     *
     * @param firstRelationOperator the firstRelationOperator to set
     */
    public void setFirstRelationOperator(String firstRelationOperator) {
        //只存储 第一个关系运算符 and or
        if (this.firstRelationOperator == null) {
            this.firstRelationOperator = firstRelationOperator;
        }
    }
}