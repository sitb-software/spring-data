package software.sitb.spring.data.mongo.repository;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("DBSequence")
public class DBSequence {

    /**
     * 序列ID
     */
    private String id;

    private Long value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
