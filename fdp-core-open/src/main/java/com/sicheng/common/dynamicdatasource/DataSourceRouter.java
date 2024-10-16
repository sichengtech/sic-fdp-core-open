package com.sicheng.common.dynamicdatasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 多数据源路由器。
 * 可实现spring多路由配置，由spring框架自动调用。
 * AbstractRoutingDataSource 是spring提供的一个多数据源抽象类。
 * DataSourceRouter继承了AbstractRoutingDataSource 抽象类。
 * spring框架会在使用事务的地方来，自动调用此类的determineCurrentLookupKey()方法来获取数据源的key值。
 */
public class DataSourceRouter extends AbstractRoutingDataSource {

    /**
     * 获取当前正在使用的数据源名称
     * @return 数据源名称
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.peek();
    }
}
