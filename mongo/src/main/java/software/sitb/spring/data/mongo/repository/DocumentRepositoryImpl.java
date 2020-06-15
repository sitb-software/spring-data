package software.sitb.spring.data.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author Sean(sean.snow @ live.com) createAt 17-12-6.
 */
public class DocumentRepositoryImpl implements DocumentRepository {

    private final MongoTemplate mongoTemplate;

    public DocumentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T> List<T> findAll(Class<T> documentClass, Query query, Sort sort) {
        query.with(sort);
        return this.mongoTemplate.find(query, documentClass);
    }

    @Override
    public <T> Page<T> findAll(Class<T> documentClass, Query query, Pageable pageable) {
        long total = 0;
        boolean noCount = true;
        if (null != pageable) {
            total = this.mongoTemplate.count(query, documentClass);
            noCount = false;
            query.with(pageable);
        }
        List<T> content = this.mongoTemplate.find(query, documentClass);
        if (noCount) {
            total = content.size();
        }

        return pageable == null ? new PageImpl<>(content) : new PageImpl<>(content, pageable, total);
    }


}
