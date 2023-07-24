/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.utils4m;

import com.sicheng.common.utils.StringUtils;

import java.util.List;

/**
 * <p>标题: ApiUtils</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cailong
 * @version 2017年12月16日 下午3:30:44
 */
public class ApiUtils {
    public static final Integer LIMIT_DEFAULT = 10;                //list长度常量

    /**
     * 获取是否传入了limit参数，如果没有limit取默认值，如果有则取传进来的参数
     *
     * @param limit 入参
     * @return
     */
    public static Integer getLimit(Object limit) {
        if (limit == null) {
            return LIMIT_DEFAULT;
        }
        if (!StringUtils.isNumeric((CharSequence) limit)) {
            throw new RuntimeException("limit不是数字");
        }
        return Integer.parseInt(String.valueOf(limit));
    }

    /**
     * 错误信息由List转成String
     *
     * @param errorList
     */
    public static String errorMessage(List<String> errorList) {
        if (errorList.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("数据验证失败：");
        for (int i = 0; i < errorList.size(); i++) {
            sb.append(errorList.get(i));
            if (i != errorList.size() - 1) {
                sb.append("<br/>");
            }
        }
        return sb.toString();
    }
}
