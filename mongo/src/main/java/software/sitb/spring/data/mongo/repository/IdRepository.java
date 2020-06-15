package software.sitb.spring.data.mongo.repository;


/**
 * @author Sean(sean.snow @ live.com) createAt 17-12-26.
 */
public interface IdRepository {

    void createSequence(String sequenceName);

    Long generateSequence(String sequenceName);

}
