package software.sitb.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * @author 田尘殇
 */
@Repository
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class EntityRepositoryImpl implements EntityRepository {

    public static final String COUNT_SELECT = "SELECT count(*) ";

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * 通过ID 查询数据
     *
     * @param domainClass 表实体类Class
     * @param id          id 值
     * @return 查询的结果
     */
    @Override
    public <T> T findOne(Class<T> domainClass, Long id) {
        return entityManager.find(domainClass, id);
    }

    @Override
    public <T> T findOne(Class<T> domainCLass, Specification<T, T> specification) {
        try {
            return getQuery(true, domainCLass, specification, (Sort) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <T> T findOneWithRandom(Class<T> domainClass, Specification<T, T> specification) {
        long count = countQuery(domainClass, (Specification<T, Long>) specification);
        if (count == 0)
            return null;
        TypedQuery<T> query = getQuery(true, domainClass, specification, (Sort) null);
        Random random = new Random();
        int first = random.nextInt((int) count);
        query.setFirstResult(first);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public <T> void save(T entity) {
        entityManager.persist(entity);
    }

    @Override
    @Transactional
    public <T> void save(T[] entities) {
        save(Arrays.asList(entities));
    }

    /**
     * 保存或者更新数据
     *
     * @param entities 数据表
     */
    @Override
    @Transactional
    public <T> void save(Collection<T> entities) {
        entities.forEach(entity -> entityManager.persist(entity));
    }

    @Override
    @Transactional
    public <T> T update(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public <T> T[] update(T[] entities) {
        return (T[]) update(Arrays.asList(entities)).toArray();
    }

    @Override
    @Transactional
    public <T> Collection<T> update(Collection<T> entities) {
        return entities.stream().map(entity -> entityManager.merge(entity)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <T> void delete(Class<T> domainClass, Long id) {
        Assert.notNull(id, "id 不能为null");

        T entity = findOne(domainClass, id);

        if (entity == null) {
            return;
        }

        delete(entity);
    }

    @Override
    public <T> void delete(T entity) {
        delete(Collections.singletonList(entity));
    }

    /**
     * 删除数据
     *
     * @param entities 数据表
     */
    @Override
    @Transactional
    public <T> void delete(T[] entities) {
        delete(Arrays.asList(entities));
    }

    /**
     * 删除数据
     *
     * @param entities 数据表
     */
    @Override
    @Transactional
    public <T> void delete(Collection<T> entities) {
        for (T entity : entities) {
            this.entityManager.remove(this.entityManager.contains(entity) ? entity : this.entityManager.merge(entity));
        }
    }

    @Override
    @Transactional
    public <T> void delete(Class<T> domainClass, Specification<T, T> specification) {
        delete(findAll(domainClass, specification));
    }

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param jpql   jpql
     * @param params 参数
     * @return 查询结果
     */
    @Override
    public <T> List<T> query(String jpql, Object... params) {
        Query query = entityManager.createQuery(jpql);
        setQueryParams(query, params);
        return query.getResultList();
    }

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param jpql   jpql
     * @param params 参数
     * @return 查询结果
     */
    @Override
    public <T> List<T> query(String jpql, Map<String, Object> params) {
        Query query = entityManager.createQuery(jpql);
        setQueryParams(query, params);
        return query.getResultList();
    }

    /**
     * 根据传入的JPQL 语句执行查询,返回第一条记录
     *
     * @param jpql   jpql
     * @param params 查询参数
     * @return 查询结果
     */
    @Override
    public <T> T queryOne(String jpql, Object... params) {
        Query query = entityManager.createQuery(jpql);
        setQueryParams(query, params);
        return executeQueryOne(query);
    }

    /**
     * 根据传入的JPQL 语句执行查询,返回第一条记录
     *
     * @param jpql   jpql
     * @param params 查询参数
     * @return 查询结果
     */
    @Override
    public <T> T queryOne(String jpql, Map<String, Object> params) {
        Query query = entityManager.createQuery(jpql);
        setQueryParams(query, params);
        return executeQueryOne(query);
    }

    /**
     * @param select   jpql语句select部分
     * @param from     jpql 语句 from到结束部分
     * @param pageable 分页信息
     * @param params   参数
     * @param <T>      AbstractEntity
     * @return 分页查询结果
     */
    @Override
    public <T> Page<T> query(String select, String from, Pageable pageable, Object... params) {
        Query query = entityManager.createQuery(select + " " + from);
        setQueryParams(query, params);

        return null == pageable ? new PageImpl<>(query.getResultList()) : readPage(from, query, pageable, params);
    }

    /**
     * 根据传入的JPQL语句查询数据
     *
     * @param select   jpql语句select部分
     * @param from     jpql 语句 from部分
     * @param pageable 分页信息
     * @param params   参数
     * @return 返回分页查询结果
     */
    @Override
    public <T> Page<T> query(String select, String from, Pageable pageable, Map<String, Object> params) {
        Query query = entityManager.createQuery(select + from);
        setQueryParams(query, params);

        return null == pageable ? new PageImpl<>(query.getResultList()) : readPage(from, query, pageable, params);
    }

    @Override
    public <T> Long countQuery(Class<T> domainClass) {
        return countQuery(domainClass, null);
    }

    @Override
    public <T> Long countQuery(Class<T> domainClass, Specification<T, Long> specification) {
        return executeCountQuery(getCountQuery(domainClass, specification));
    }

    @Override
    public <R, T> List<R> query(Class<R> resultClass, Class<T> domainClass, Specification<T, R> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> query = builder.createQuery(resultClass);
        applySpecificationToCriteria(domainClass, specification, query);
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <R, T> R queryOne(Class<R> resultClass, Class<T> domainClass, Specification<T, R> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> query = builder.createQuery(resultClass);
        applySpecificationToCriteria(domainClass, specification, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public <T> List<T> findAll(Class<T> domainClass) {
        return getQuery(true, domainClass, null, (Sort) null).getResultList();
    }

    @Override
    public <T> List<T> findAll(Class<T> domainClass, Specification<T, T> specification) {
        return getQuery(true, domainClass, specification, (Sort) null).getResultList();
    }

    @Override
    public <T> List<T> findAllMultiSelect(Class<T> domainClass, Specification<T, T> specification) {
        return getQuery(false, domainClass, specification, (Sort) null).getResultList();
    }

    @Override
    public <T> Page<T> findAll(Class<T> domainClass, Specification<T, T> spec, Pageable pageable) {
        TypedQuery<T> query = getQuery(true, domainClass, spec, pageable);
        return pageable == null ? new PageImpl<>(query.getResultList()) : readPage(domainClass, query, pageable, spec);
    }

    @Transactional
    @Override
    public int executeUpdate(String updateJpql, Object... params) {
        Query query = this.entityManager.createQuery(updateJpql);
        setQueryParams(query, params);
        return query.executeUpdate();
    }

    @Override
    public int executeUpdateInBatch(String updateJpql, Object... params) {
        return executeUpdate(updateJpql, params);
    }

    @Override
    public int executeUpdate(String updateJpql, Map<String, Object> params) {
        Query query = this.entityManager.createQuery(updateJpql);
        setQueryParams(query, params);
        return query.executeUpdate();
    }

    @Override
    public <T> boolean exists(Class<T> domainClass, Specification<T, Long> specification) {
        Long count = countQuery(domainClass, specification);
        return null != count && count > 0;
    }

    @Override
    public <T> List<T> findAll(Class<T> domainClass, Specification<T, T> spec, Sort sort) {
        return getQuery(true, domainClass, spec, sort).getResultList();
    }

    @Override
    public Object getNative() {
        return this.entityManager;
    }

    private <T> Page<T> readPage(String from, Query query, Pageable pageable, Object... params) {
        Long total = executeCountQuery(getCountQuery(from, params));
        return executeReadPage(query, pageable, total);
    }

    private <T> Page<T> readPage(Class<T> domainClass, TypedQuery<T> query, Pageable pageable, Specification spec) {
        Long total = executeCountQuery(getCountQuery(domainClass, spec));
        return executeReadPage(query, pageable, total);
    }

    private <T> Page<T> executeReadPage(Query query, Pageable pageable, Long total) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.emptyList();
        if (content == null)
            content = Collections.emptyList();

        return new PageImpl<>(content, pageable, total);
    }

    private TypedQuery<Long> getCountQuery(String from, Object... params) {
        TypedQuery<Long> query = entityManager.createQuery(COUNT_SELECT + from, Long.class);
        setQueryParams(query, params);
        return query;
    }

    private <T> TypedQuery<Long> getCountQuery(Class<T> domainClass, Specification<T, Long> spec) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = applySpecificationToCriteria(domainClass, spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return entityManager.createQuery(query);
    }

    private <T> TypedQuery<T> getQuery(boolean selectAll, Class<T> domainClass, Specification<T, T> specification, Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);

        Root<T> root = applySpecificationToCriteria(domainClass, specification, query);
        if (selectAll) {
            query.select(root);
        }

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return entityManager.createQuery(query);
    }

    private <T> TypedQuery<T> getQuery(boolean selectAll, Class<T> domainClass, Specification<T, T> spec, Pageable pageable) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(selectAll, domainClass, spec, sort);
    }

    private <R, T> Root<T> applySpecificationToCriteria(Class<T> domainClass, Specification<T, R> specification, CriteriaQuery<R> query) {
        Root<T> root = query.from(domainClass);

        if (specification == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = specification.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    /**
     * Executes a count query and transparently sums up all values returned.
     *
     * @param query must not be {@literal null}.
     * @return Long
     */
    private Long executeCountQuery(TypedQuery<Long> query) {
        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private <T> T executeQueryOne(Query query) {
        T result = null;
        try {
            result = (T) query.getSingleResult();
        } catch (Exception ignored) {
        }
        return result;
    }

    private void setQueryParams(Query query, Object... params) {
        int i = 0;
        for (Object param : params) {
            query.setParameter(i++, param);
        }
    }

    private void setQueryParams(Query query, Map<String, Object> params) {
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
    }

}
