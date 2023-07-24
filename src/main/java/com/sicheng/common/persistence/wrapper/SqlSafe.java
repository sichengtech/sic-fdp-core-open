/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.wrapper;

import com.sicheng.common.utils.StringUtils;

import java.util.regex.Pattern;

/**
 * <p>标题: SqlSafe</p>
 * <p>描述: 防止SQL注入攻击的类</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年2月1日 下午3:30:07
 */
public class SqlSafe {

    // 检查SQL注入的正则表达式
    // 被检查的目标：被检查的目标不是一句完整的SQL，完整的SQL不好做检查。例如：select * from t1 where name='张三'，因为它包含的信息太多。
    // 所以要拆分成更细的粒度来检查。被检查的是where条件部分的“一个条件”,例如：name='张三'。
    // 正常情况，Wrapper强制使用{0}{1}占位符(隐式占位符也算占位符)来传参数，所以传来的是sql片段是“name={0}”这样的，肯定不包含以下字符。
    // 若其中包含以下字符，就说明有风险存在，疑似SQL注入攻击。
    //
    // 下面正则的意思是：是否包含 ' (单引号)，是否包含 ORACLE注释--和/**/，是否包含SQL关键字select,update,delete,and,or .... 等
    private static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(?:#)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";


    /**
     * 安全检查，未通过检查则会抛出异常 。
     * 检查两个方面：
     * 1、是否存在sql注入攻击的风险。
     * 2、是否使用了{0}{1}占位符(含Wrapper显式占位符、Wrapper隐式占位符)。要求：必须使用占位符，禁止拼接SQL
     *
     * @param element 被检查的where条件和参数值
     * @return true:安全，false:不安全
     */
    public static boolean securityCheck(SqlConditionElement element) {
        if (element == null) {
            return true;
        }

        String content = element.getCondition();//where条件
        Object[] args = element.getValues();//参数的值

        if (StringUtils.isBlank(content)) {
            return true;
        }

        //安全检查--是否使用了占位符检查
        //Wrapper强制使用{0}{1}占位符(隐式占位符也算占位符)来传参数，禁止拼接SQL
        boolean hasOper = SqlSafe.hasOperator(content);
        if (hasOper) {

            String[] arr = {"exists"};
            //这是针对exists语句的临时方案，放过对exists语句的“是否使用了占位符检查”
            //由于exists中可以有子句，子句有运算符但可以无参数，例如：wrapper.exists("select 1 form table where id=a.p_id");

            boolean contain = false;
            for (String s : arr) {
                if (content.toLowerCase().contains(s.toLowerCase())) {
                    contain = true;
                    break;
                }
            }

            if (!contain) {
                int paramSize = 0;//参数的数量
                if (args != null) {
                    paramSize = args.length;
                }
                if (paramSize == 0) {
                    throw new WrapperException("Wrapper类的" + content + "条件必需有参数，禁止拼接SQL，业务被终止");
                }
            }
        }
        //安全检查--防sql注入攻击行为
        boolean safe = SqlSafe.checkSqlInjection(content);
        if (safe) {
            throw new WrapperException("Wrapper类的" + content + "条件中发现有sql注入攻击行为，业务被终止");
        }
        return true;
    }

    /**
     * 检查SQL片段是否存在sql注入攻击的风险。
     *
     * @param sqlParam SQL片段
     * @return true:有风险,false:无风险
     */
    public static boolean checkSqlInjection(String sqlParam) {
        if (sqlParam == null || "".equals(sqlParam.trim())) {
            return false;
        }

        boolean isBetween = false;
        boolean isExists = false;
        boolean base = false;
        if (sqlParam.toLowerCase().contains("between")) {
            isBetween = true;
        } else if (sqlParam.toLowerCase().contains("exists")) {
            isExists = true;
        } else {
            base = true;
        }

        String regNew = reg;//基础正则，验证规则最严格
        if (base) {
            regNew = reg;
        } else if (isBetween) {
            // 为适应between语句，reg在基础上去掉"and"
            regNew = reg.replace("and|", "");
        } else if (isExists) {
            // 为适应exists语句，reg在基础上去掉"and"、"or"、"select"
            regNew = reg.replace("and|", "").replace("or|", "").replace("select|", "");
        }

        Pattern sqlPattern = Pattern.compile(regNew, Pattern.CASE_INSENSITIVE);//Pattern.CASE_INSENSITIVE 让表达式忽略大小写进行匹配
        if (sqlPattern.matcher(sqlParam).find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串中是包含有sql的专用运算符
     *
     * @param sqlParam
     * @return
     */
    private static boolean hasOperator(String sqlParam) {
        if (sqlParam == null || "".equals(sqlParam.trim())) {
            return false;
        }

        //检查字符串中是否包含以下值
        //只有 is null 、is not null 运算符，不需要参数。exists可能不需要参数
        //下面是sql的专用运算符，当有这些运算符时，都需要参数1-2个参数
        String s = "!=,=,<>,>,>=,<,<=, like , in ,not in, between ,not between";
        String[] arr = s.toLowerCase().split(",");
        for (String f : arr) {
            int index = sqlParam.toLowerCase().indexOf(f);
            if (index != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * SQL注入内容剥离
     *
     * @param sqlParam SQL的参数
     * @return 剥离后的参数串
     */
    public static String stripSqlInjection(String sqlParam) {
        return sqlParam.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }
}
