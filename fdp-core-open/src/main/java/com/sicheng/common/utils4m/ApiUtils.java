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
package com.sicheng.common.utils4m;

import com.sicheng.common.utils.StringUtils;

import java.util.List;

/**
 * <p>标题: ApiUtils</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cl
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
