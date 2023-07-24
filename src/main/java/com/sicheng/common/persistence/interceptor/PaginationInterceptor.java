/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.interceptor;

import com.sicheng.common.persistence.Page;
import com.sicheng.common.utils.Reflections;
import com.sicheng.common.utils.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;
import java.util.Properties;

/**
 * 数据库分页插件 (目前正在使用)
 * 拦截Executor的query方法，动态的修改SQL
 *
 * @author 赵磊
 * @version 2013-8-28
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 1L;
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    /**
     * 拦截器主拦截方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Object parameterObject = boundSql.getParameterObject();
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];

        // 获取分页对象
        Page<Object> page = null;
        if (parameterObject != null) {
            page = convertParameter(parameterObject, page);
        }

        //检查，放过
        if (page == null) {
            //如果无page对象，说明不是分页sql不需要被拦截，直接放过走旧有逻辑
            return invocation.proceed();
        }

        //检查，放过
        if (page.getPageSize() == -1) {
            //pageSize=-1 表示不分页，直接放过走旧有逻辑
            return invocation.proceed();
        }

        //检查，放过
        if (StringUtils.isBlank(boundSql.getSql())) {
            return null;
        }

        //////////////////////
        //下面开始处理分页逻辑
        //////////////////////

        // 原始的sql语句
        String originalSql = boundSql.getSql().trim();

        // 生成分页查询的SQL语句
        String pageSql = originalSql;

        // 处理总记录数
        if (page.isNotCount()) {
            // count=-1 表示不查询总数，可用于提高性能,但不能显示“总记录数”，超出后会查不出数据。
            // 适海量数据的分页，如有100万条数据，页面上只有一个“下一页”按钮，用户一直点就一直翻页。直到返空数据时停止。
            page.setCount(page.getPageNo() * page.getPageSize());
        } else if (page.getCount() > 0) {
            // count>0 表示已通过其它途径获得了conut总条数，不再需要再查询总条数，节省性能。
            // 适海量数据的分页，如有100万条数据，用户不可能从第1页翻到最后一页，可以只让用户查前100页(2000条)。
        } else {
            // 分离最后一个拦截器代理对象的目标类,countSql需要使用缓存，需要metaObject
            MetaObject metaExecutor = util(invocation);

            //  检查是否包含启用“分页二次偏移量算法”的暗示语法
            String hint = SQLHelper.hint4LimitOffset(originalSql);

            if (hint == null) {
                // 无暗示语法，走常规路线，默认使用最最普通的常规分页逻辑。90%会走这里。

                //将一条普通的业务sql转换为 求count的SQL
                String countSql = DIALECT.countSql(originalSql);
                //执行一个求count总数的SQL，并返回一个值：总记录数
                int count = SQLHelper.execSQL(countSql, null, mappedStatement, parameterObject, boundSql, log, metaExecutor, rowBounds);
            page.setCount(count);

                // 生成常规分页查询SQL,就是最常见的分页SQL, DIALECT可按不同数据库类型可生成此库专用的SQL
                pageSql = DIALECT.pagedSql(originalSql, page.getFirstResult(), page.getMaxResults());

            } else {
                //有暗示语法，启用“分页二次偏移量算法”，10%会走这里。
                //一对多的多表join的SQL,导致左表记录数变多、分页不准、合并后不足一页的问题,将通过用新方法计算出来偏移量来解决。

                //将一条普通的业务sql转换为支持“二次偏移量算法”的求count的SQL(第一类)
                String countSql = DIALECT.countSql4joinOffset(originalSql, hint);
                //执行一个求count总数的SQL，并返回一个值：总记录数。这个总记录数才是真实的。
                int count = SQLHelper.execSQL(countSql, null, mappedStatement, parameterObject, boundSql, log, metaExecutor, rowBounds);
                page.setCount(count);

                //将一条普通的业务sql转换为支持“二次偏移量算法”的求count的SQL(第二类)(第一次，注意最后两个参数有变化)
                int offset=0;
                if(page.getFirstResult()==0){
                    //如果是每一页page.getFirstResult()为0，计算出来的offset也为0，就不执行count sql了，直接给0值就可以。
                    offset=0;
                }else{
                    String countSql4joinOffset1 = DIALECT.countSql4joinOffset(originalSql, hint, 0, page.getFirstResult());
                    //执行一个求count总数的SQL，并返回一个值：总记录数。这才是真实的offset偏移量
                    offset = SQLHelper.execSQL(countSql4joinOffset1, null, mappedStatement, parameterObject, boundSql, log, metaExecutor, rowBounds);
                }

                //将一条普通的业务sql转换为支持“二次偏移量算法”的求count的SQL(第二类)(第二次，注意最后两个参数有变化)
                String countSql4joinOffset2 = DIALECT.countSql4joinOffset(originalSql, hint, page.getFirstResult(), page.getMaxResults());
                //执行一个求count总数的SQL，并返回一个值：总记录数。这才是真实的limit偏移量
                int limit = SQLHelper.execSQL(countSql4joinOffset2, null, mappedStatement, parameterObject, boundSql, log, metaExecutor, rowBounds);

                // 生成分页查询SQL,使用了“二次偏移量”,DIALECT可按不同数据库可生成此库专用的SQL
                pageSql = DIALECT.pagedSql(originalSql, offset, limit);//offset, limit是二次偏移后的值，是计算后正确的值。
            }
        }

        invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());

        // 解决mapper.xml文件使用bind指令或foreach指令新绑定的参数取不到问题  赵磊修改2017-10-25
        @SuppressWarnings("unchecked")
        Map<String, Object> addMap = (Map<String, Object>) Reflections.getFieldValue(boundSql, "additionalParameters");
        for (String name : addMap.keySet()) {
            newBoundSql.setAdditionalParameter(name, addMap.get(name));
        }

        //copy
        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        invocation.getArgs()[0] = newMs;

        //执行后续的业务逻辑
        return invocation.proceed();
    }


    /**
     * 分离最后一个拦截器代理对象的目标类
     * 使用反射工具
     *
     * @param invocation
     * @return
     */
    public MetaObject util(Invocation invocation) {
        Executor executorProxy = (Executor) invocation.getTarget();
        //MetaObject是反射工具
        //MetaObject是Mybatis提供的一个用于方便、优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，
        //同时它支持对JavaBean、Collection、Map三种类型对象的操作。
        MetaObject metaExecutor = MetaObject.forObject(executorProxy, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        // 分离拦截器代理对象链，有可能配置了多个拦截器
        while (metaExecutor.hasGetter("h")) {
            Object object = metaExecutor.getValue("h");
            metaExecutor = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个拦截器代理对象的目标类，有可能配置了多个拦截器
        while (metaExecutor.hasGetter("target")) {
            Object object = metaExecutor.getValue("target");
            metaExecutor = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        return metaExecutor;
    }

    @Override
    public Object plugin(Object target) {
        // 对target目标进行拦截
        return Plugin.wrap(target, this);
    }

    /**
     * <p>设置属性，支持自定义方言类和制定数据库的方式 </p>
     *
     * @param properties
     * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties properties) {
        super.initProperties(properties);
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        builder.useCache(ms.isUseCache());// 2017赵磊添加，修复分页的SQL未走缓存的bug
        return builder.build();
    }

    /**
     * 内部类
     */
    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
