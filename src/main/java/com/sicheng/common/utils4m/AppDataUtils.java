/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.utils4m;

import com.sicheng.common.mapper.JsonMapper;
import com.sicheng.common.persistence.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>标题: AppDataUtils</p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author cailong
 * @version 2017年12月16日 下午3:30:44
 */
public class AppDataUtils {

    public static final String STATUS_OK = "200";                //服务器成功返回用户请求的数据
    public static final String STATUS_INVALID = "400";            //用户发出的请求有错误，服务器没有进行新建或修改数据的操作
    public static final String STATUS_UNAUTHORIZED = "401";        //未登录
    public static final String STATUS_SIGN_ERROR = "402";        //签名错误
    public static final String STATUS_FORBIDDEN = "403";        //无权限
    public static final String STATUS_NOT_FOUND = "404";        //用户发出的请求针对的是不存在的记录，服务器没有进行操作
    public static final String STATUS_NOT_ACCEPTABLE = "406";    //用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）
    public static final String STATUS_SERVER_ERROR = "500";        //服务器发生错误，用户将无法判断发出的请求是否成功


    /**
     * 封装数据包装体
     * 按《统一数据包装体和状态码》的要求封装数据包装体，封装于map当中
     *
     * @param status  状态码
     * @param message 提示信息
     * @param data    业务数据
     * @param page    page分页对象
     * @return Map<String, Object>
     */
    public static AppData getAppData(String status, String message, Object data, Page<?> page) {
        AppData appData = new AppData();
        appData.status = status;
        appData.message = message;
        //分页页码信息
        if (page != null) {
            SimplePage smallPage = new SimplePage();
            appData.page = smallPage;
            smallPage.pageNo = page.getPageNo();//获取当前页码
            smallPage.totalPage = page.getTotalPage();//总页码
            smallPage.pageSize = page.getPageSize();//页大小
            smallPage.count = page.getCount();//总条数
            smallPage.isFirstPage = page.isFirstPage();
            smallPage.isLastPage = page.isLastPage();
            smallPage.isBeyondPage = page.isBeyondPage();//输入的页码超过总页数
        }
        appData.data = data;
        return appData;
    }

    /**
     * 封装数据包装体
     * 按《统一数据包装体和状态码》的要求封装数据包装体，封装于map当中
     *
     * @param status  状态码
     * @param message 提示信息
     * @param data    业务数据
     * @param page    page分页对象
     * @return Map<String, Object>
     */
    public static Map<String, Object> getMap(String status, String message, Object data, Page<?> page) {
        Map<String, Object> map = new LinkedHashMap<>();


        map.put("status", status);//状态码
        map.put("message", message);//文字提示信息
        map.put("data", data);//原始业务数据

        //分页页码信息
        if (page != null) {
            Map<String, Object> pageMap = new LinkedHashMap<>();
            pageMap.put("pageNo", page.getPageNo());//获取当前页码
            pageMap.put("totalPage", page.getTotalPage());//总页码
            pageMap.put("pageSize", page.getPageSize());//页大小
            pageMap.put("count", page.getCount());//总条数
            pageMap.put("isFirstPage", page.isFirstPage());
            pageMap.put("isLastPage", page.isLastPage());
            pageMap.put("isBeyondPage", page.isBeyondPage());//输入的页码超过总页数
            map.put("page", pageMap);
        }

        return map;
    }

    /**
     * 封装数据包装体
     * 按《统一数据包装体和状态码》的要求封装数据包装体，封装json
     *
     * @param status  状态码
     * @param message 提示信息
     * @param data    业务数据
     * @param page    page分页对象
     * @return Map<String, Object>
     */
    public static String getJson(String status, String message, Object data, Page<?> page) {
        Map<String, Object> map = getMap(status, message, data, page);
        return JsonMapper.getInstance().toJson(map);
    }


    /**
     * API接口专用的数据包装体
     */
    @ApiModel(value = "AppData" ,description = "包装体" )
    public static class AppData<T> {
        @ApiModelProperty(value = "状态码")
        String status;
        @ApiModelProperty(value = "消息")
        String message;
        @ApiModelProperty(name="SimplePage",value = "分页数据", dataType = "SimplePage")
        SimplePage page;
        @ApiModelProperty(name="XXX",value = "业务数据")
        T data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public SimplePage getPage() {
            return page;
        }

        public void setPage(SimplePage page) {
            this.page = page;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    /**
     * 简化版本分页数据
     */
    @ApiModel(value = "SimplePage", description = "简单分页")
    public static class SimplePage {
        @ApiModelProperty(value = "当前页码")
        int pageNo;//获取当前页码
        @ApiModelProperty(value = "总页码")
        int totalPage;//总页码
        @ApiModelProperty(value = "页大小")
        int pageSize;//页大小
        @ApiModelProperty(value = "总条数")
        long count;//总条数
        @ApiModelProperty(value = "是否是第一页")
        boolean isFirstPage;
        @ApiModelProperty(value = "是否是最后一页")
        boolean isLastPage;
        @ApiModelProperty(value = "是否输入的页码超过总页数")
        boolean isBeyondPage;//输入的页码超过总页数

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public boolean isFirstPage() {
            return isFirstPage;
        }

        public void setFirstPage(boolean firstPage) {
            isFirstPage = firstPage;
        }

        public boolean isLastPage() {
            return isLastPage;
        }

        public void setLastPage(boolean lastPage) {
            isLastPage = lastPage;
        }

        public boolean isBeyondPage() {
            return isBeyondPage;
        }

        public void setBeyondPage(boolean beyondPage) {
            isBeyondPage = beyondPage;
        }
    }
}
