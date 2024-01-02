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
package com.sicheng.common.persistence.cache;

import com.sicheng.common.security.Digests;
import com.sicheng.common.security.MD5;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * <p>标题: CacheKeyUtils</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年7月5日 下午8:42:18
 */
public class CacheKeyUtils {
    /**
     * 对key进行缩短处理
     * MyBatis 对于其 Key 的生成采取规则为：[hashcode : checksum : mappedStementId : offset : limit : executeSql : queryParams]
     * 生成的key包含sql语句，长度很长。对key用md5算法散列，达到缩短的目的。
     *
     * @param key
     * @return
     */
    public static String creadkey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key=null");
        }
        boolean compressKey = true;//是否缩短key
        if (compressKey) {
            return MD5.encrypt(key.toString().getBytes());
        } else {
            //美化sql,去掉多余的空格、回车等符号，在输出日志时方便阅读
            return removeBreakingWhitespace(key.toString());
        }
    }

    //性能测试
    public static void main(String[] a) throws UnsupportedEncodingException {
        int times = 5000;
        String key = "SELECT a.user_name AS 'userName', a.store_name AS 'storeName', a.province_id AS 'provinceId', a.city_id AS 'cityId', a.province_name AS 'provinceName', a.city_name AS 'cityName', a.category_name AS 'categoryName', a.category_level AS 'categoryLevel', a.cate_first_letter AS 'cateFirstLetter', a.cate_parent_ids AS 'cateParentIds', a.store_cate_name AS 'storeCateName', a.is_open AS 'isOpen', a.store_cate_parent_ids AS 'storeCateParentIds', a.brand_name AS 'brandName', a.brand_first_leftter AS 'brandFirstLeftter', a.brand_english_name AS 'brandEnglishName', a.all_sales AS 'allSales', a.week_sales AS 'weekSales', a.month_sales AS 'monthSales', a.month3_sales AS 'month3Sales', a.car_ids AS 'carIds', a.param_value AS 'paramValue', a.collection_count AS 'collectionCount', a.comment_count AS 'commentCount', a.p_id AS 'pId', a.u_id AS 'uId', a.store_id AS 'storeId', a.name AS 'name', a.status AS 'status', a.category_id AS 'categoryId', a.store_category_id AS 'storeCategoryId', a.image AS 'image', a.brand_id AS 'brandId', a.name_sub AS 'nameSub', a.unit AS 'unit', a.type AS 'type', a.is_gift AS 'isGift', a.benefit AS 'benefit', a.is_recommend AS 'isRecommend', a.recommend_sort AS 'recommendSort', a.market_price AS 'marketPrice', a.point AS 'point', a.max_price AS 'maxPrice', a.min_price AS 'minPrice', a.action AS 'action', a.max_price1 AS 'maxPrice1', a.min_price1 AS 'minPrice1', a.max_price2 AS 'maxPrice2', a.min_price2 AS 'minPrice2', a.shelf_time AS 'shelfTime', a.create_date AS 'createDate', a.update_date AS 'updateDate' FROM solr_product a WHERE (p_id= ?) ORDER BY a.create_date DESC ";

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            CacheKeyUtils.creadkey(key);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("总耗时：" + (t2 - t1) + "ms，平均：" + ((float) (t2 - t1)) / times + "ms");

        long t3 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Digests.md5(key.getBytes());
        }
        long t4 = System.currentTimeMillis();
        System.out.println("总耗时：" + (t4 - t3) + "ms，平均：" + ((float) (t4 - t3)) / times + "ms");
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
}
