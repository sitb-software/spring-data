package software.sitb.spring.data.mybatis.dialect;

public abstract class Dialect {


    /**
     * 返回分页sql，无占位符，limit和 offset 直接写死在sql中
     */

    public abstract String getLimitString(String sql, int offset, int limit);


    /**
     * 将sql转换为总记录数SQL
     *
     * @param sql SQL语句
     * @return 总记录数的sql
     */

    public String getCountString(String sql) {
        return "select count(1) from (" + sql + ") tmp_count";
    }

}