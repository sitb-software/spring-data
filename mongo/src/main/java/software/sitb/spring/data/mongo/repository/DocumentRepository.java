package software.sitb.spring.data.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author Sean(sean.snow@live.com) createAt 17-12-6.
 */
public interface DocumentRepository {

    <T> List<T> findAll(Class<T> documentClass, Query query, Sort sort);

    <T> Page<T> findAll(Class<T> documentClass, Query query, Pageable pageable);

}
