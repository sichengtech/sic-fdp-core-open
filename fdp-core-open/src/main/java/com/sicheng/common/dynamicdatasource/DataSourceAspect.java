package com.sicheng.common.dynamicdatasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * 用于切换数据源的AOP拦截器，AOP 拦截service并切换数据源
 * 在某个service方法上通过@DS注解指明数据源名称以后，通过本 AOP拦截所有service方法，在方法执行之前获取方法上的注解：即数据源的key值，完成切换数据源。
 */
//@Aspect
//@Component
//@Order(1) //请注意：这里order一定要小于tx:annotation-driven的order，即先执行DataSourceAspect切面，再执行事务切面，才能获取到最终的数据源
//@EnableAspectJAutoProxy(exposeProxy = true) //开启AspectJ自动代理，是需要的。不然AopContext.currentProxy()无法工作。
public class DataSourceAspect {
    static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    /**
     * AOP切面，再进入方法之前，检查是否有 @DS注解，如果有则改变数据源，再执行方法之后，则清空恢复数据源
     * 切入点 service包及子孙包下的所有类
     *
     * @param point
     * @return
     * @throws Throwable
     */
    public Object around(ProceedingJoinPoint point) throws Throwable{
        Class<?> target = point.getTarget().getClass();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod() ;
        DS dataSource = null ;
        //先从"方法"上、“类”上找注释。
        dataSource = this.getDataSource(target, method) ; //获取 方法或类 上的  @DS注解对象
        if(dataSource == null){
            //如果类上没找到，则向上从接口这一层上找注释。
            for (Class<?> clazz : target.getInterfaces()) {
                dataSource = getDataSource(clazz, method); //获取 方法或类 上的  @DS注解对象
                if(dataSource != null){
                    break ;//从某个接口中一旦发现注解，退出循环
                }
            }
        }

        if(dataSource != null && !StringUtils.isEmpty(dataSource.value()) ){
            //重点：设置要使用的数据源，dataSource.value()是数据源的名称
            DynamicDataSourceContextHolder.push(dataSource.value());
            try {
                //执行原业务逻辑
                return point.proceed();
            } finally {
                //重点：清空当前数据源
                DynamicDataSourceContextHolder.poll();
            }
        }else{
            //执行原业务逻辑
            //如果在service层的类上、方法没有找到@DS注解，就什么也不做，使用默认的数据源。
            return point.proceed();
        }
    }

    /**
     * 获取 方法或类 上的  @DS注解对象
     * @param target    类class
     * @param method    方法
     * @return DS @DS注解对象
     */
    public DS getDataSource(Class<?> target, Method method){
        try {
            //1.优先在方法上找注解
            Class<?>[] types = method.getParameterTypes();
            Method m = target.getMethod(method.getName(), types);
            if (m != null && m.isAnnotationPresent(DS.class)) {
                return m.getAnnotation(DS.class);
            }
            //2.其次在类上找注解
            if (target.isAnnotationPresent(DS.class)) {
                return target.getAnnotation(DS.class);
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("通过@DS注解切换数据源时发生异常[class={0},method={1}]："
                    , target.getName(), method.getName()),e)  ;
        }
        return null ;
    }
}
