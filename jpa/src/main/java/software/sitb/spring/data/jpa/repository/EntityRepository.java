package software.sitb.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * 公共数据查询仓库
 *
 * @author 田尘殇
 * date 2015/5/22
 * time 13:18
 */
public interface EntityRepository {

    /**
     * 保存数据
     *
     * @param entities 数据表
     */
    void save(Object... entities);

    /**
     * 保存数据
     *
     * @param entities 数据表
     */
    void save(Iterable<?> entities);

    /**
     * 更新数据
     *
     * @param entities 数据表
     */
    void update(Object... entities);

    void update(Iterable<?> entities);

    /**
     * 通过id删除数据
     *
     * @param id id
     */
    void delete(Class<?> domainClass, Object id);

    /**
     * 删除数据
     *
     * @param entities 数据表
     */
    void delete(Object... entities);

    /**
     * 删除数据
     *
     * @param entities 数据表
     */
    void delete(Iterable<?> entities);

    <T> void delete(Class<T> domainClass, Specification<T, T> specification);

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param jpql   jpql
     * @param params 参数
     * @param <T>    结果类型
     * @return 查询结果
     */
    <T> List<T> query(String jpql, Object[] params);

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param jpql   jpql
     * @param params 参数
     * @param <T>    实体
     * @return 查询结果
     */
    <T> List<T> query(String jpql, Map<String, Object> params);

    /**
     * 根据传入的JPQL 语句执行查询,返回第一条记录
     *
     * @param jpql   jpql
     * @param params 查询参数
     * @param <T>    查询结果类型
     * @return 查询结果
     */
    <T> T queryOne(String jpql, Object... params);

    /**
     * 根据传入的JPQL 语句执行查询,返回第一条记录
     *
     * @param jpql   jpql
     * @param params 查询参数
     * @param <T>    查询结果类型
     * @return 查询结果
     */
    <T> T queryOne(String jpql, Map<String, Object> params);

    /**
     * @param select   jpql语句select部分
     * @param from     jpql 语句 from到结束部分
     * @param pageable 分页信息
     * @param params   参数
     * @return 分页查询结果
     * @see EntityRepository#query(String, String, Pageable, String, Object[])
     */
    @Deprecated
    <T> Page<T> query(String select, String from, Pageable pageable, Object... params);

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param select   select 部分预计
     * @param from     from 后面语句
     * @param pageable 分页排序信息
     * @param alias    排序表别名
     * @param params   参数
     * @param <T>      返回结果类型
     * @return 返回结果
     */
    <T> Page<T> query(String select, String from, Pageable pageable, String alias, Object[] params);

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param select   jpql语句select部分
     * @param from     jpql 语句 from部分
     * @param pageable 分页信息
     * @param params   参数
     * @return 分页查询结果
     */
    <T> Page<T> query(String select, String from, Pageable pageable, Map<String, Object> params);

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param select   jpql语句select部分
     * @param from     jpql 语句 from部分
     * @param pageable 分页信息
     * @param params   参数
     * @param alias    排序表别名
     * @return 分页查询结果
     */
    <T> Page<T> query(String select, String from, Pageable pageable, Map<String, Object> params, String alias);

    <T> Long countQuery(Class<T> domainClass);

    <T> Long countQuery(Class<T> domainClass, Specification<T, Long> specification);

    /**
     * 通过ID 查询数据
     *
     * @param domainClass 表实体类Class
     * @param id          id 值
     * @param <T>         实际表类型
     * @return 查询的结果
     */
    <T> T findOne(Class<T> domainClass, Object id);

    <T> T findOne(Class<T> domainCLass, Specification<T, T> specification);

    /**
     * 随机查询一条数据
     *
     * @param domainClass   表实体Class
     * @param specification 条件
     * @param <T>           表类型
     * @return 随机数据
     */
    <T> T findOneWithRandom(Class<T> domainClass, Specification<T, T> specification);

    <T> List<T> findAll(Class<T> domainClass);

    <T> List<T> findAll(Class<T> domainClass, Specification<T, T> specification);

    <T> List<T> findAll(Class<T> domainClass, Specification<T, T> specification, Sort sort);

    <T> Page<T> findAll(Class<T> domainClass, Specification<T, T> specification, Pageable pageable);

    /**
     * 查询所有数据,可在条件中自定义select
     * 需要有对应的实体构造器
     *
     * @param domainClass   实体Class
     * @param specification 查询条件
     * @param <T>           实体类型
     * @return 查询结果集
     */
    <T> List<T> findAllCustomSelect(Class<T> domainClass, Specification<T, T> specification);

    <T> List<T> findAllCustomSelect(Class<T> domainClass, Specification<T, T> specification, Sort sort);

    <T> Page<T> findAllCustomSelect(Class<T> domainClass, Specification<T, T> specification, Pageable pageable);

    <T, R> List<R> findAllCustomSelect(Class<T> domainClass, Class<R> resultClass, Specification<T, R> specification);

    <T, R> List<R> findAllCustomSelect(Class<T> domainClass, Class<R> resultClass, Specification<T, R> specification, Sort sort);

    <T, R> Page<R> findAllCustomSelect(Class<T> domainClass, Class<R> resultClass, Specification<T, R> specification, Pageable pageable);

    <T> T findOneCustomSelect(Class<T> domainClass, Specification<T, T> specification);

    <T> T findOneCustomSelect(Class<T> domainClass, Specification<T, T> specification, Sort sort);

    <T, R> R findOneCustomSelect(Class<T> domainClass, Class<R> resultClass, Specification<T, R> specification);

    <T, R> R findOneCustomSelect(Class<T> domainClass, Class<R> resultClass, Specification<T, R> specification, Sort sort);

    int executeUpdate(String updateJpql, Object... params);

    int executeUpdateInBatch(String updateJpql, Object... params);

    int executeUpdate(String updateJpql, Map<String, Object> params);

    /**
     * 判断数据是否存在
     *
     * @param domainClass   实体class
     * @param specification 查询条件
     * @param <T>           表类型
     * @return 数据存在返回true, 不存在返回false
     */
    <T> boolean exists(Class<T> domainClass, Specification<T, Long> specification);

    /**
     * 获取原生对象
     *
     * @return native
     */
    Object getNative();
}
