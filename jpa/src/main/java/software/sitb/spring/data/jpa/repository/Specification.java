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

    Predicate toPredicate(Root<T> root, CriteriaQuery<R> query, CriteriaBuilder cb);
}
