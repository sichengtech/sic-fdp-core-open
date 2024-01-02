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
package com.sicheng.common.service;

import com.sicheng.common.cache.ShopCache;
import com.sicheng.common.persistence.CrudDao;
import com.sicheng.common.persistence.DataEntity;
import com.sicheng.common.persistence.Page;
import com.sicheng.common.persistence.wrapper.Wrapper;
import com.sicheng.common.utils.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>标题:Service基类 </p>
 * <p>描述: </p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @param <D>
 * @param <T>
 * @author zhaolei
 * @date 2016年5月15日 下午9:19:04
 */
@Transactional(propagation = Propagation.SUPPORTS)
public abstract class CrudService<D extends CrudDao<T>, T extends DataEntity<T>> extends BaseService {

    private static int BATCH_SIZE = 50;//批量执行SQL时，每一批的默认大小

    /**
     * sqlSessionFactory
     */
    @Autowired
    SqlSessionFactoryBean sqlSessionFactory;

    /**
     * 缓存接口
     */
    @Autowired
    protected ShopCache cache;

    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;

    /**
     * 获取单条数据
     *
     * @param id 主键
     * @return
     */
    public T selectById(Long id) {
        return dao.selectById(id);
    }

    /**
     * 获取单条数据
     *
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public T selectOne(Wrapper wrapper) {
        List<T> list = dao.selectByWhere(null, wrapper);
        if (list.isEmpty()) {
            return null;
        }
        int size = list.size();
        if (size > 1) {
            logger.warn("Warn: 执行selectOne语句查出{}个结果，应该只查出1个结果.", size);
        }
        return list.get(0);
    }

    /**
     * 查询 select * from a where id in(…)
     *
     * @param list 主键集合
     * @return
     */
    public List<T> selectByIdIn(List<?> list) {
        return dao.selectByIdIn(list);
    }

    /**
     * 查询分页数据
     * 查询数据列表，支持分页，分页对象可控制order by 排序
     *
     * @param page    分页对象
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public Page<T> selectByWhere(Page<T> page, Wrapper wrapper) {
        List<T> list = dao.selectByWhere(page, wrapper);
        if (page == null) {
            page = new Page<T>();
        }
        page.setList(list);
        return page;
    }

    /**
     * 查询列表数据
     * 查询数据列表，不分页
     *
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> selectByWhere(Wrapper wrapper) {
        return dao.selectByWhere(null, wrapper);
    }

    /**
     * 查询所有数据列表，无条件
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    public List<T> selectAll(Wrapper wrapper) {
        return dao.selectAll(wrapper);
    }

    /**
     * 插入或更新数据
     * 1、当无ID时，被认为是新数据，做插入
     * 2、当有ID时，做更新处理，更新成功则结束。更新不到再使用此ID做插入处理
     *
     * @param entity
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(T entity) {
        if (entity.getIsNewRecord()) {
            //当无ID时，被认为是新数据，做插入
            return this.insertSelective(entity);
        } else {
            //当有ID时，做更新处理，更新成功则结束
            int rs = this.updateByIdSelective(entity);
            if (rs == 1) {
                return rs;
            }
            //更新不到再使用此ID做插入处理
            entity.setPkMode(1);
            return this.insertSelective(entity);
        }
    }

    /**
     * 插入数据
     * 如果要在entity中带回自增长生成的主键值，mybatis的xml中要添加<insert id="insertSelective" keyProperty="pageId" useGeneratedKeys="true">
     *
     * @param entity
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int insert(T entity) {
        entity.preInsert();
        return dao.insert(entity);
    }

    /**
     * 插入,只把非空的值插入到对应的字段
     * 如果要在entity中带回自增长生成的主键值，mybatis的xml中要添加<insert id="insertSelective" keyProperty="pageId" useGeneratedKeys="true">
     *
     * @param entity
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertSelective(T entity) {
        entity.preInsert();
        return dao.insertSelective(entity);
    }

    /**
     * 根据主键更新记录,更新除了主键的所有字段
     *
     * @param entity
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateById(T entity) {
        entity.preUpdate();
        return dao.updateById(entity);
    }

    /**
     * 根据条件更新记录,更新除了主键的所有字段
     *
     * @param entity  数据实体，用于存储数据，这些数据将被update到表中
     * @param wrapper 条件，用于where条件，找出符合条件的数据
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateByWhere(T entity, Wrapper wrapper) {
        entity.preUpdate();
        return dao.updateByWhere(entity, wrapper);
    }

    /**
     * 修改数据(主键)
     * 根据主键更新记录,只把非空的值更到对应的字段
     *
     * @param entity
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateByIdSelective(T entity) {
        entity.preUpdate();
        return dao.updateByIdSelective(entity);
    }

    /**
     * 修改数据(条件)
     * 根据条件更新记录,只把非空的值更到对应的字段
     *
     * @param entity  数据实体，用于存储数据，这些数据将被update到表中
     * @param wrapper 条件，用于where条件，找出符合条件的数据
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateByWhereSelective(T entity, Wrapper wrapper) {
        entity.preUpdate();
        return dao.updateByWhereSelective(entity, wrapper);
    }

    /**
     * 根据主键删除记录
     *
     * @param id
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {
        return dao.deleteById(id);
    }

    /**
     * 根据主键批量删除记录
     *
     * @param list 一批主键
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIdIn(List<Object> list) {
        return dao.deleteByIdIn(list);
    }

    /**
     * 根据条件删除记录
     *
     * @param wrapper
     * @return 受影响的行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByWhere(Wrapper wrapper) {
        return dao.deleteByWhere(wrapper);
    }

    /**
     * 根据条件查询记录总条数
     *
     * @param wrapper 可为null。或new一个Wrapper对象，用于控制del_flag、控制distinct
     * @return 总行数
     */
    public int countByWhere(Wrapper wrapper) {
        return dao.countByWhere(wrapper);
    }

    /**
     * 批量insert
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(List<T> entityList) {
        String sqlId = "insert";
        return batch(entityList, sqlId, BATCH_SIZE, 1);
    }

    /**
     * 批量insert，只把非空的值更到对应的字段
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSelectiveBatch(List<T> entityList) {
        String sqlId = "insertSelective";
        return batch(entityList, sqlId, BATCH_SIZE, 1);
    }

    /**
     * 批量update
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<T> entityList) {
        String sqlId = "updateById";
        return batch(entityList, sqlId, BATCH_SIZE, 2);
    }

    /**
     * 批量update，只把非空的值更到对应的字段
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelectiveBatch(List<T> entityList) {
        String sqlId = "updateByIdSelective";
        return batch(entityList, sqlId, BATCH_SIZE, 2);
    }

    /**
     * 批量执行insert、update的基础方法
     * 底层使用preparestatement，实现的真的批量插入和更新，高性能，适合各种数据库
     * 若使用oracle数据库，oracle jdbc驱动版本不能低于ojdbc6-12.1.0.2，否则批量插入无法运行。
     * <p>
     * xxxSelective方法，是只把非空的值更到对应的字段，会引起每一条preparestatement语句的参数数量是变化的，SQL语句是变化的.
     * Mybatis容错式的解决了这个问题，每当preparestatement语句的SQL变化时，就执行新的SQL语句，这保证了功能的正确性，稍影响性能。
     * 所以，请你按常规套路来使用batch，可收到最佳效果。
     * 常规套路是：如一批SQL有100条，每一条preparestatement语句的参数数量应是一致，SQL语句是不变化的，只有参数的值是可以变的。
     *
     * @param entityList 实体对象的List
     * @param sqlId      SQL语句的名称，是在mapper xml文件中的id，必需写对，此ID的SQL必须是真实存在的。
     * @param batchSize  每一批的大小
     * @param method     方法，1插入，2更新
     * @return
     * @author zhaolei 2017-1-29 (正月初二)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batch(List<T> entityList, String sqlId, int batchSize, int method) {
        if (entityList == null || entityList.isEmpty()) {
            return false;
        }
        if (StringUtils.isBlank(sqlId)) {
            throw new IllegalArgumentException("Error: sqlName must not be empty");
        }
        if (batchSize <= 0) {
            batchSize = BATCH_SIZE;
        }
        if (method != 1 && method != 2) {
            throw new IllegalArgumentException("Error: method 参数错误，只能是1或2");
        }
        try {
            SqlSessionFactory factory = sqlSessionFactory.getObject();
            SqlSession batchSqlSession = factory.openSession(ExecutorType.BATCH, false);
            int size = entityList.size();
            //通过反射获取Dao接口类的名称，作为mybatis的命名空间名称
            String currentNamespace = Reflections.getClassGenricType(getClass(), 0).getName();
            StringBuffer statement = new StringBuffer();
            statement.append(currentNamespace);
            statement.append(".");
            statement.append(sqlId);
            String sqlNameSpace = statement.toString();
            for (int i = 0; i < size; i++) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                //2017-10-10 蔡龙
                //为了在调用batch方法的同时可以把createDate updateDate 时间插入进去
                entityList.get(i).preInsert();
                paramMap.put("entity", entityList.get(i));
                if (method == 1) {
                    batchSqlSession.insert(sqlNameSpace, paramMap);//执行insert语句
                } else {
                    batchSqlSession.update(sqlNameSpace, paramMap);//执行update语句
                }
                if ((i + 1) % batchSize == 0) {
                    //sqlSession.flushStatements();起到一种预插入的作用。
                    //(执行了这行代码之后,要插入的数据会锁定数据库的一行记录,
                    //并把数据库默认返回的主键赋值给插入的对象,这样就可以把该对象的主键赋值给其他需要的对象中去了)
                    batchSqlSession.flushStatements();
                }
            }
            batchSqlSession.flushStatements();

            //modify by 赵磊 2017-09-10
            //问：这里为什么要明确调用commit?
            //答：为了在启用二级缓存的背景下，批量插入或更新sql执行完成后，能清理二级缓存。
            //CachingExecutor、BatchExecutor、BaseExecutor，都有commit方法，一般的SQL执行成功后都会触发调用commit方法。
            //但是CrudService类的batch方法执行成功后，未调用到commit方法(数据确实已成功入库)，导致未能清理二级缓存，从而产生脏数据。
            batchSqlSession.commit();
        } catch (Exception e) {

            if (method == 1) {
                logger.error("Error: Cannot execute insertBatch Method.", e);
            } else {
                logger.error("Error: Cannot execute updateBatch Method.", e);
            }
            return false;
        }
        return true;
    }

    /**
     * 查询并返回Cusror游标
     * <p>
     * 在mybatis 3.4.0版本中新增了一个功能，查询可以返回Cusror<T>类型的数据，
     * 类似于JDBC里的ResultSet类，当查询百万级的数据的时候，使用游标可以节省内存的消耗，
     * 不需要一次性取出所有数据，可使用Cusror游标按批取出，再处理业务。
     *
     * @param sqlId   SQL语句的名称，是在mapper xml文件中的id，必需写对，此ID的SQL必须是真实存在的。
     * @param wrapper 查询条件，可为null。或new一个Wrapper对象，用于控制order by 排序、控制del_flag、控制distinct
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Cursor<T> selectCursor(String sqlId, Wrapper wrapper) {
        if (StringUtils.isBlank(sqlId)) {
            throw new IllegalArgumentException("Error: sqlName must not be empty");
        }
        SqlSessionFactory factory = null;
        try {
            factory = sqlSessionFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException("获取SqlSessionFactory异常", e);
        }
        SqlSession sqlSession = factory.openSession(false);

        //生成sqlId
        StringBuffer statement = new StringBuffer();
        if (sqlId.contains(".")) {
            //sqlId是带命名空间的，是完整的sqlId,
            //例如："com.sicheng.admin.product.dao.ProductSpuDao.selectByWhere"
            //不需要处理，可直接使用
            statement.append(sqlId);
        } else {
            //sqlId是不带命名空间的，要生成默认的命名空间，并接接上
            //通过反射获取Dao接口类的名称，作为mybatis的命名空间名称
            String currentNamespace = Reflections.getClassGenricType(getClass(), 0).getName();
            statement.append(currentNamespace);
            statement.append(".");
            statement.append(sqlId);
        }
        String sqlNameSpace = statement.toString();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("w", wrapper);
        Cursor<T> cursor = sqlSession.selectCursor(sqlNameSpace, paramMap);//游标
        return cursor;
    }

}
