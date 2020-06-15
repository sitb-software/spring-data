package software.sitb.spring.data.mongo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


/**
 * @author Sean(sean.snow @ live.com) createAt 17-12-26.
 */
public class MongodbIdRepository implements IdRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Long generateSequence(String sequenceName) {
        return generateNoSqlSequence(sequenceName);
    }

    @Override
    public void createSequence(String sequenceName) {
        save(sequenceName);
    }

    private Long generateNoSqlSequence(String sequenceName) {
        DBSequence seq = mongoTemplate.findAndModify(
                new Query(Criteria.where("id").is(sequenceName)),
                new Update().inc("value", 1L),
                DBSequence.class
        );
        if (null == seq) {
            DBSequence init = save(sequenceName);
            return init.getValue();
        }
        return seq.getValue();
    }

    private DBSequence save(String sequenceName) {
        DBSequence exists = mongoTemplate.findOne(
                new Query(Criteria.where("id").is(sequenceName)),
                DBSequence.class
        );
        if (null == exists) {
            DBSequence sequence = new DBSequence();
            sequence.setId(sequenceName);
            sequence.setValue(1L);
            return mongoTemplate.insert(sequence);
        }
        return exists;
    }

}
