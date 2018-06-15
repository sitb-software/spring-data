package software.sitb.spring.data.mongo.repository;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;

public class CriteriaBuilder {

    public static final CriteriaBuilder instance = new CriteriaBuilder();

    private CriteriaBuilder() {
    }

    public Criteria is(String key, Object value) {
        return Criteria.where(key).is(value);
    }

    public Criteria in(String k, Object... v) {
        return Criteria.where(k).in(v);
    }

    public Criteria in(String k, Collection<?> v) {
        return Criteria.where(k).in(v);
    }

    public Criteria regex(String key, String value) {
        return Criteria.where(key).regex(value);
    }

    public Criteria like(String key, String value) {
        return regex(key, ".*" + value + ".*");
    }

    public Criteria leftLike(String key, String value) {
        return regex(key, ".*" + value);
    }

    public Criteria rightLike(String key, String value) {
        return regex(key, value + ".*");
    }

    public Criteria elemMatch(String k, Criteria c) {
        return new Criteria(k).elemMatch(c);
    }
}