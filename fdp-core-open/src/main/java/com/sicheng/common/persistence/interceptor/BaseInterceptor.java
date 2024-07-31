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
package com.sicheng.common.persistence.interceptor;

import com.sicheng.common.config.Global;
import com.sicheng.common.persistence.Page;
import com.sicheng.common.persistence.dialect.Dialect;
import com.sicheng.common.persistence.dialect.db.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * Mybatis分页拦截器基类
 *
 * @author zhaolei
 * @version 2013-8-28
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {

    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    private static final long serialVersionUID = 1L;

    protected static final String PAGE = "page";

    protected static final String DELEGATE = "delegate";

    protected static final String MAPPED_STATEMENT = "mappedStatement";

    protected Log log = LogFactory.getLog(this.getClass());

    protected Dialect DIALECT;

    /**
     * 拦截的ID，在mapper中的id，可以匹配正则
     * 停用
     */
    //protected String _SQL_PATTERN = "";

    /**
     * 对参数进行转换和检查,从中取出Page对象
     *
     * @param parameterObject 参数对象
     * @param page            分页对象
     * @return 分页对象
     */
    @SuppressWarnings("unchecked")
    protected static Page<Object> convertParameter(Object parameterObject, Page<Object> page) {
        try {
            //如果入参是Page类型
            if (parameterObject instanceof Page) {
                return (Page<Object>) parameterObject;
            }

            //如果入参是Map类型
            if (parameterObject instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) parameterObject;
                if (map != null) {
                    for (String key : map.keySet()) {
                        Object paramObj = map.get(key);//从map中取实体，再从实体中取page
                        if (paramObj instanceof Page) {
                            return (Page<Object>) paramObj;
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("分页--对参数进行转换和检查,从中取出Page对象时异常", e);
            return null;
        }
    }

    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <code>dialectClass</code>,自定义方言类。可以不配置这项
     * <ode>dbms</ode> 数据库类型，插件支持的数据库
     * <code>sqlPattern</code> 需要拦截的SQL ID
     *
     * @param p 属性
     */
    protected void initProperties(Properties p) {
        Dialect dialect = null;
        String dbType = Global.getConfig("jdbc.type");
        if ("db2".equals(dbType)) {
            dialect = new DB2Dialect();
        } else if ("derby".equals(dbType)) {
            dialect = new DerbyDialect();
        } else if ("h2".equals(dbType)) {
            dialect = new H2Dialect();
        } else if ("hsql".equals(dbType)) {
            dialect = new HSQLDialect();
        } else if ("mysql".equals(dbType)) {
            dialect = new MySQLDialect();
        } else if ("oracle".equals(dbType)) {
            dialect = new OracleDialect();
        } else if ("postgre".equals(dbType)) {
            dialect = new PostgreSQLDialect();
        } else if ("mssql".equals(dbType) || "sqlserver".equals(dbType)) {
            dialect = new SQLServer2005Dialect();
        } else if ("sybase".equals(dbType)) {
            dialect = new SybaseDialect();
        }
        if (dialect == null) {
            throw new RuntimeException("mybatis dialect error.");
        }
        DIALECT = dialect;
    }
}
