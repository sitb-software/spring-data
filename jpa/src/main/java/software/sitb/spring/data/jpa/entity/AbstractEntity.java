package software.sitb.spring.data.jpa.entity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 基础表实体
 *
 * @author Sean sean.snow@live.com
 *         2015/12/9
 */
@MappedSuperclass
public abstract class AbstractEntity implements java.io.Serializable {

    private static final long serialVersionUID = 9069438494941423064L;

    public abstract <ID extends Serializable> ID getId();

    public abstract <ID extends Serializable> void setId(ID id);

    /**
     * 返回该实体序列名字
     *
     * @return 序列名
     */
    public String sequence() {
        return generateSequenceName(this.getClass());
    }

    protected <T extends AbstractEntity> String generateSequenceName(Class<T> entity) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return entity.getSimpleName().replaceAll(regex, replacement).toLowerCase() + "_seq";
    }

}
