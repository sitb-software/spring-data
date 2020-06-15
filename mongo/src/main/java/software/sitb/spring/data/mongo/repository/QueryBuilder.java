package software.sitb.spring.data.mongo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Sean(sean.snow @ live.com) createAt 17-12-13.
 */
public class QueryBuilder {

    private final Query query;

    public final CriteriaBuilder criteria = CriteriaBuilder.instance;

    private QueryBuilder() {
        query = new Query();
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    public QueryBuilder is(String k, Object v) {
        query.addCriteria(criteria.is(k, v));
        return this;
    }

    public QueryBuilder in(String field, Object... v) {
        query.addCriteria(Criteria.where(field).in(v));
        return this;
    }

    public QueryBuilder in(String field, Collection<?> v) {
        query.addCriteria(Criteria.where(field).in(v));
        return this;
    }

    public QueryBuilder nin(String field, Object... v) {
        query.addCriteria(Criteria.where(field).nin(v));
        return this;
    }

    public QueryBuilder nin(String field, Collection<?> v) {
        query.addCriteria(Criteria.where(field).nin(v));
        return this;
    }

    public QueryBuilder regex(String field, String regex) {
        query.addCriteria(Criteria.where(field).regex(regex));
        return this;
    }

    public QueryBuilder regex(String field, Pattern regex) {
        query.addCriteria(Criteria.where(field).regex(regex));
        return this;
    }

    public QueryBuilder like(String key, String value) {
        regex(key, ".*" + value + ".*");
        return this;
    }

    public QueryBuilder leftLike(String key, String value) {
        query.addCriteria(criteria.leftLike(key, value));
        return this;
    }

    public QueryBuilder rightLike(String key, String value) {
        query.addCriteria(criteria.rightLike(key, value));
        return this;
    }

    public QueryBuilder gt(String field, Object v) {
        query.addCriteria(Criteria.where(field).gt(v));
        return this;
    }

    public QueryBuilder gte(String field, Object v) {
        query.addCriteria(Criteria.where(field).gte(v));
        return this;
    }

    public QueryBuilder lt(String field, Object v) {
        query.addCriteria(Criteria.where(field).lt(v));
        return this;
    }

    public QueryBuilder lte(String field, Object v) {
        query.addCriteria(Criteria.where(field).lte(v));
        return this;
    }

    public QueryBuilder between(String field, Object v1, Object v2) {
        query.addCriteria(Criteria.where(field).gte(v1).lte(v2));
        return this;
    }

    public QueryBuilder or(Criteria... criteria) {
        query.addCriteria(new Criteria().orOperator(criteria));
        return this;
    }

    public QueryBuilder and(Criteria... criteria) {
        query.addCriteria(new Criteria().andOperator(criteria));
        return this;
    }

    public QueryBuilder nor(Criteria... criteria) {
        query.addCriteria(new Criteria().norOperator(criteria));
        return this;
    }

    public QueryBuilder not(String field) {
        query.addCriteria(Criteria.where(field).not());
        return this;
    }

    public QueryBuilder ne(String field, Object o) {
        query.addCriteria(Criteria.where(field).ne(o));
        return this;
    }


    public QueryBuilder exists(String key, boolean exists) {
        query.addCriteria(Criteria.where(key).exists(exists));
        return this;
    }

    public QueryBuilder position(String field, int value) {
        Field f = query.fields();
        f.position(field, value);
        return this;
    }

    public QueryBuilder exclude(String... keys) {
        Field f = query.fields();
        for (String key : keys) {
            f.exclude(key);
        }
        return this;
    }

    public QueryBuilder include(String... keys) {
        Field f = query.fields();
        for (String key : keys) {
            f.include(key);
        }
        return this;
    }

    public QueryBuilder sort(Sort sort) {
        query.with(sort);
        return this;
    }

    public QueryBuilder page(Pageable pageable) {
        query.with(pageable);
        return this;
    }

    public QueryBuilder addCriteria(CriteriaDefinition criteriaDefinition) {
        query.addCriteria(criteriaDefinition);
        return this;
    }

    public Query build() {
        return query;
    }

}
