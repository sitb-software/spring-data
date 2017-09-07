package software.sitb.spring.data.jpa.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @param <T> 实体数据类型
 * @param <R> 查询结果类型
 */
public interface Specification<T, R> {
    /**
     * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
     * {@link Root} and {@link CriteriaQuery}.
     *
     * @param root  root
     * @param query query
     * @return a {@link Predicate}, must not be {@literal null}.
     */
    Predicate toPredicate(Root<T> root, CriteriaQuery<R> query, CriteriaBuilder cb);
}
